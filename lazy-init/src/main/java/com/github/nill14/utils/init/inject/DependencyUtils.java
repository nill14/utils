package com.github.nill14.utils.init.inject;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IQualifiedProvider;
import com.github.nill14.utils.init.impl.MethodPojoFactory;
import com.github.nill14.utils.init.impl.ProviderTypePojoFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

public enum DependencyUtils {
	;
	
	public static Map<IParameterType, Boolean> collectDependencies(IBeanDescriptor<?> descriptor) {
		return ImmutableMap.copyOf(transformDependencies(collectDependencyTypes(descriptor)));
	}
	
	public static Map<IParameterType, Boolean> collectDependencies(IMemberDescriptor descriptor) {
		return ImmutableMap.copyOf(transformDependencies(descriptor.getParameterTypes().stream()));
	}
	
	private static Stream<IParameterType> collectDependencyTypes(IBeanDescriptor<?> descriptor) {
		Stream<IMemberDescriptor> members = Stream.concat(descriptor.getFieldDescriptors().stream(), 
				Stream.concat(descriptor.getMethodDescriptors().stream(), 
						descriptor.getConstructorDescriptors().stream()));
		return members
				.flatMap(f -> f.getParameterTypes().stream());
	}
	
	private static Map<IParameterType, Boolean> transformDependencies(Stream<IParameterType> types) {
		Map<IParameterType, Boolean> result = Maps.newHashMap();
		
		Iterable<IParameterType> iterable = types::iterator;
		for (IParameterType type : iterable) {
			boolean isRequired = isRequired(type);
			
			while (type.isCollection() || type.isOptional() || isProvider(type) || isQualifiedProvider(type)) { 
				isRequired &= isRequired(type);
				type = type.getFirstParamType();
			} 
			
			Boolean prevRequired = result.get(type);
			// if value is not stored yet
			// or prev value is false and current is true then result is true
			if (prevRequired == null || (isRequired && !prevRequired)) {
				result.put(type, isRequired);
			}
		}

		return result;
	}

	private static boolean isRequired(IParameterType type) {
		return !type.isCollection() && !type.isOptional() && !type.isNullable() && !isQualifiedProvider(type);
	}
	
	private static boolean isProvider(IParameterType type) {
		return Provider.class.equals(type.getRawType());
	}
	
	private static boolean isQualifiedProvider(IParameterType type) {
		return IQualifiedProvider.class.equals(type.getRawType());
	}
	
	private static boolean isBeanInjector(IParameterType type) {
		return IBeanInjector.class.equals(type.getRawType());
	}
	
	
	private static void mergeDependencies(Set<TypeToken<?>> requiredDependencies, Set<TypeToken<?>> optionalDependencies, 
			Map<IParameterType, Boolean> pojoDependencies, Predicate<BindingKey<?>> requiresAnalyse, boolean parentRequired) {
		
		for (Entry<IParameterType, Boolean> dep : pojoDependencies.entrySet()) {
			boolean isRequired = parentRequired && dep.getValue();
			IParameterType type = dep.getKey();
			TypeToken<?> token = type.getToken();
			
			if (!isExcludedFromDependencies(type)) {
				if (isRequired) {
					requiredDependencies.add(token);
				} else {
					optionalDependencies.add(token);
				}
			}

			if (requiresAnalyse.test(type.getBindingKey())) {
				IBeanDescriptor<Object> typeDescriptor = new PojoInjectionDescriptor<>(type);
				Map<IParameterType, Boolean> pojoDependencies2 = DependencyUtils.collectDependencies(typeDescriptor);
				mergeDependencies(requiredDependencies, optionalDependencies, pojoDependencies2, requiresAnalyse, isRequired);
			}
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	private static void mergeDependencies(Set<TypeToken<?>> requiredDependencies, Set<TypeToken<?>> optionalDependencies, Predicate<BindingKey<?>> requiresAnalyse, IPojoFactory<?> pojoFactory) {
		
		if (pojoFactory instanceof ProviderTypePojoFactory) {
			IPojoFactory<?> nestedPojoFactory = ((ProviderTypePojoFactory) pojoFactory).getNestedPojoFactory();
			mergeDependencies(requiredDependencies, optionalDependencies, requiresAnalyse, nestedPojoFactory);
		
		} else if (pojoFactory instanceof MethodPojoFactory) {
			Map<IParameterType, Boolean> collectDependencies = DependencyUtils.collectDependencies(
					((MethodPojoFactory) pojoFactory).getMethodDescriptor()); 
			mergeDependencies(requiredDependencies, optionalDependencies, collectDependencies, requiresAnalyse, true);
		}
		
		Map<IParameterType, Boolean> pojoDependencies = DependencyUtils.collectDependencies(pojoFactory.getDescriptor());
		mergeDependencies(requiredDependencies, optionalDependencies, pojoDependencies, requiresAnalyse, true);
	}
	
	private static boolean isExcludedFromDependencies(IParameterType type) {
		return isBeanInjector(type) || isQualifiedProvider(type) || isProvider(type);
	}
	
	/**
	 * 
	 * @param pojoFactories 
	 * @param requiresAnalyse 
	 * @return a map where key is a dependency (e.g. @Inject) and value is whether the dependency is required. 
	 */
	public static Map<TypeToken<?>, Boolean> collectDependencies(Collection<IPojoFactory<?>> pojoFactories, Predicate<BindingKey<?>> requiresAnalyse) {
		Set<TypeToken<?>> requiredDependencies = Sets.newHashSet();
		Set<TypeToken<?>> optionalDependencies = Sets.newHashSet();
		
		for (IPojoFactory<?> pojoFactory : pojoFactories) {
			mergeDependencies(requiredDependencies, optionalDependencies, requiresAnalyse, pojoFactory);
		}
		
		optionalDependencies.removeAll(requiredDependencies);
		ImmutableMap.Builder<TypeToken<?>, Boolean> builder = ImmutableMap.builder(); 
		for (TypeToken<?> token : optionalDependencies) {
			builder.put(token, false);
		}
		for (TypeToken<?> token : requiredDependencies) {
			builder.put(token, true);
		}
		
		return builder.build();
	}
	
	public static void splitDependencies(Map<TypeToken<?>, Boolean> dependencies, Set<Class<?>> requiredDependencies, Set<Class<?>> optionalDependencies) {
		
		for (Entry<TypeToken<?>, Boolean> dep : dependencies.entrySet()) {
			boolean isRequired = dep.getValue();
			TypeToken<?> token = dep.getKey();
			if (isRequired) {
				requiredDependencies.add(token.getRawType());
			} else {
				optionalDependencies.add(token.getRawType());
			}
		}
	}
}

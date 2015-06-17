package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Provider;

import com.github.nill14.utils.annotation.Experimental;
import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IQualifiedProvider;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.target.BeanTypeBindingTarget;
import com.github.nill14.utils.init.binding.target.LinkedBindingTarget;
import com.github.nill14.utils.init.binding.target.PojoFactoryBindingTargetVisitor;
import com.github.nill14.utils.init.binding.target.UnscopedProvider;
import com.github.nill14.utils.init.inject.DependencyUtils;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

@Experimental
public class SimplePropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {
	
	private static final long serialVersionUID = 6151314354173527220L;
	
	private final ImmutableMap<BindingKey<?>, BindingImpl<?>> bindings;
	private final Multimap<TypeToken<?>, BindingImpl<?>> typeBindings = ArrayListMultimap.create();
	private final ImmutableMap<BindingTarget<?>, IPojoFactory<?>> bindingFactories;
	

	public SimplePropertyResolver(ImmutableList<BindingImpl<?>> bindings, ChainingPojoInitializer initializer) {
		super(initializer);
		this.bindings = prepareBindings(bindings);
		this.bindingFactories = prepareFactories(this.bindings.values());
	}

	
	public SimplePropertyResolver(ImmutableList<BindingImpl<?>> bindings, ChainingPropertyResolver parent) {
		super(parent);
		this.bindings = prepareBindings(bindings);
		this.bindingFactories = prepareFactories(this.bindings.values());
	}

	private ImmutableMap<BindingTarget<?>, IPojoFactory<?>> prepareFactories(
			Collection<BindingImpl<?>> bindings) {
		
		Set<BindingTarget<?>> targets = bindings.stream()
			.map(b -> b.getBindingTarget())
			.filter(t -> !(t instanceof LinkedBindingTarget))
			.collect(Collectors.toSet());
		
		ImmutableMap.Builder<BindingTarget<?>, IPojoFactory<?>> builder = ImmutableMap.builder();
		PojoFactoryBindingTargetVisitor bindingTargetVisitor = new PojoFactoryBindingTargetVisitor(); 
		for (BindingTarget<?> target : targets) {
			IPojoFactory<?> pojoFactory = target.accept(bindingTargetVisitor);
			builder.put(target, pojoFactory);
		}
		
		return builder.build();
	}


	private final ImmutableMap<BindingKey<?>, BindingImpl<?>> prepareBindings(ImmutableList<BindingImpl<?>> bindings)  {
		Map<BindingKey<?>, BindingImpl<?>> rawBindingCandidates = Maps.newHashMap();
		Map<BindingKey<?>, BindingImpl<?>> map = Maps.newHashMap();
		for (BindingImpl<?> binding : bindings) {
			BindingKey<?> key = binding.getBindingKey();
			boolean occupied = map.putIfAbsent(key, binding) != null; //TODO scopes? (identical key, different scope - not possible now)
			
			if (occupied && key.getQualifier() != null) {
				throw new RuntimeException(String.format("Duplicate key: %s", key));
				
			} else if (occupied) {
				binding = binding.keyWithQualifier(CounterImpl.next());
				key = binding.getBindingKey();
				boolean error = map.putIfAbsent(key, binding) != null;
				if (error) {
					throw new RuntimeException("Duplicate key: " + binding);
				}
			}
			
			typeBindings.put(key.getToken(), binding);
			rawBindingCandidates.putIfAbsent(key.withQualifier(null), binding);
		}
		
		//add raw/unqualified bindings if they are not already present
		for (Entry<BindingKey<?>, BindingImpl<?>> entry : rawBindingCandidates.entrySet()) {
			map.putIfAbsent(entry.getKey(), entry.getValue());
		}
		
		map = replaceLinkedBindings(map);
		return ImmutableMap.copyOf(map);
	}

	@Override
	protected Object findByName(String name, IParameterType type) {
		return null;
	}

	@Override
	protected Object findByType(IParameterType type) {
		return simple(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<?> findAllByType(IParameterType type) {
		Collection<BindingImpl<?>> bindings = typeBindings.get(type.getToken());
		ImmutableList.Builder<Object> builder = ImmutableList.builder();
		for (BindingImpl<?> binding : bindings) {
			IScope scope = binding.getScope();
			IPojoFactory<Object> pojoFactory = (IPojoFactory<Object>) bindingFactories.get(binding.getBindingTarget());
			
			//TODO provide scope with info about "calling" scope
			UnscopedProvider<Object> provider = new UnscopedProvider<>(resolver, pojoFactory); 
			Provider<?> scopedProvider = scope.scope((BindingKey<Object>) binding.getBindingKey(), provider);
			Object element = scopedProvider.get();
			if (element != null) {
				builder.add(element);
			}
		}
		return builder.build();
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier) {
		return simple(type);
	}
	
	@SuppressWarnings("unchecked")
	private Object simple(IParameterType type) {
		BindingKey<?> bindingKey = type.getBindingKey();
		
		if (bindings.containsKey(bindingKey)) {
			BindingImpl<?> binding = bindings.get(bindingKey);
			binding = findLinkedBinding(binding);
			IScope scope = binding.getScope();
			IPojoFactory<Object> pojoFactory = (IPojoFactory<Object>) bindingFactories.get(binding.getBindingTarget());
			
			UnscopedProvider<Object> provider = new UnscopedProvider<>(resolver, pojoFactory);
			Provider<?> scopedProvider = scope.scope((BindingKey<Object>) binding.getBindingKey(), provider);
			
			return scopedProvider.get();
		} else {
//			return doPrototype(type);
			return null; //prototyping might eventually be done by ChainingPropertyResolver
		}
	}
	
	private BindingImpl<?> findLinkedBinding(BindingImpl<?> binding) {
		IScope scope = binding.getScope();
		
		if (binding.getBindingTarget() instanceof LinkedBindingTarget) {
			BindingKey<?> bindingKey2 = ((LinkedBindingTarget<?>) binding.getBindingTarget()).getBindingKey();
			BindingImpl<?> binding2 = bindings.get(bindingKey2);
			if (binding2 != null && binding2.getScope() == scope) {
//				return findLinkedBinding(binding2);
				return binding2;
			}
			else {
				throw new RuntimeException(String.format("LinkedBinding %s without target %s", binding, bindingKey2));
			}
		}
		return binding;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<BindingKey<?>, BindingImpl<?>> replaceLinkedBindings(Map<BindingKey<?>, BindingImpl<?>> map) {
		
		Collection<BindingImpl<?>> bindings = map.values();
		for (BindingImpl<?> binding : bindings) {
			BindingKey targetKey = findLinkedKey(map, binding);
			if (targetKey != binding.getBindingKey()) {
				binding = binding.withLinkedBinding(targetKey);
				map.put(binding.getBindingKey(), binding);
			}
		}
		
		return map;
	}

	
	private BindingKey<?> findLinkedKey(Map<BindingKey<?>, BindingImpl<?>> map, BindingImpl<?> binding) {
		if (binding.getBindingTarget() instanceof BeanTypeBindingTarget) {
			TypeToken<?> token = ((BeanTypeBindingTarget<?>) binding.getBindingTarget()).getToken();
			BindingKey<?> bindingKey = BindingKey.of(token);
			BindingImpl<?> linkedBinding = map.get(bindingKey);
			if (binding != linkedBinding && linkedBinding != null && linkedBinding.getScope() == binding.getScope()) {
				return findLinkedKey(map, linkedBinding);
			}
		
		} else if (binding.getBindingTarget() instanceof LinkedBindingTarget) {
			return ((LinkedBindingTarget<?>) binding.getBindingTarget()).getBindingKey();
		}
		return binding.getBindingKey();
	}	

	
	private void mergeDependencies(Set<TypeToken<?>> requiredDependencies, Set<TypeToken<?>> optionalDependencies, Map<IParameterType, Boolean> pojoDependencies, boolean parentRequired) {
		
		for (Entry<IParameterType, Boolean> dep : pojoDependencies.entrySet()) {
			boolean isRequired = parentRequired && dep.getValue();
			IParameterType type = dep.getKey();
			TypeToken<?> token = type.getToken();
			
			if (!isExcludedFromDependencies(token)) {
				if (isRequired) {
					requiredDependencies.add(token);
				} else {
					optionalDependencies.add(token);
				}
			}

			BindingImpl<?> binding = bindings.get(type.getBindingKey());
			if (binding == null) {
				IBeanDescriptor<Object> typeDescriptor = new PojoInjectionDescriptor<>(type);
				if (typeDescriptor.canBeInstantiated()) {
					//TODO transitive optional -> required == optional
					Map<IParameterType, Boolean> pojoDependencies2 = DependencyUtils.collectDependencies(typeDescriptor);
					mergeDependencies(requiredDependencies, optionalDependencies, pojoDependencies2, isRequired);
				}
			}
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	private void mergeDependencies(Set<TypeToken<?>> requiredDependencies, Set<TypeToken<?>> optionalDependencies, IPojoFactory<?> pojoFactory) {
		
		if (pojoFactory instanceof ProviderTypePojoFactory) {
			IPojoFactory<?> nestedPojoFactory = ((ProviderTypePojoFactory) pojoFactory).getNestedPojoFactory();
			mergeDependencies(requiredDependencies, optionalDependencies, nestedPojoFactory);
		
		} else if (pojoFactory instanceof MethodPojoFactory) {
			Map<IParameterType, Boolean> collectDependencies = DependencyUtils.collectDependencies(
					((MethodPojoFactory) pojoFactory).getMethodDescriptor()); 
			mergeDependencies(requiredDependencies, optionalDependencies, collectDependencies, true);
		}
		
		Map<IParameterType, Boolean> pojoDependencies = DependencyUtils.collectDependencies(pojoFactory.getDescriptor());
		mergeDependencies(requiredDependencies, optionalDependencies, pojoDependencies, true);
	}
	
	private static boolean isExcludedFromDependencies(TypeToken<?> token) {
		Class<?> rawType = token.getRawType();
		return IBeanInjector.class.equals(rawType) || 
			   IQualifiedProvider.class.equals(rawType) || 
			   Provider.class.equals(rawType);
	}


	/**
	 * 
	 * @return a map where key is a dependency (e.g. @Inject) and value is whether the dependency is required. 
	 */
	public Map<TypeToken<?>, Boolean> collectDependencies() {
		Set<TypeToken<?>> requiredDependencies = Sets.newHashSet();
		Set<TypeToken<?>> optionalDependencies = Sets.newHashSet();
		
		for (IPojoFactory<?> pojoFactory : bindingFactories.values()) {
			mergeDependencies(requiredDependencies, optionalDependencies, pojoFactory);
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
}

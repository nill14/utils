package com.github.nill14.utils.init.inject;

import java.util.Map;
import java.util.stream.Stream;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IQualifiedProvider;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
}

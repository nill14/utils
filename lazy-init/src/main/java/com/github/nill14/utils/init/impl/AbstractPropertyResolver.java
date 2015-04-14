package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IQualifiedProvider;
import com.github.nill14.utils.init.inject.ParameterTypeInjectionDescriptor;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

@SuppressWarnings("serial")
public abstract class AbstractPropertyResolver implements IPropertyResolver {
	
	@Override
	public Object resolve(IParameterType<?> type) {
		Class<?> rawType = type.getRawType();
		
		boolean isCollection = isCollection(type);
		if (isCollection || type.isOptional()) { 
			Class<?> paramClass = type.getFirstParamToken().getRawType();

			if (java.util.Optional.class.isAssignableFrom(rawType)) {
				return java.util.Optional.ofNullable(doResolve(type, paramClass));
			}
			
			if (com.google.common.base.Optional.class.isAssignableFrom(rawType)) {
				return com.google.common.base.Optional.fromNullable(doResolve(type, paramClass));
			}
			
			if (Iterable.class.isAssignableFrom(rawType)) {
				return doResolveCollection(rawType, paramClass);
			}
		} 
	
		Object result = doResolve(type, rawType);
		if (result != null) {
			return result;
		}

		IBeanDescriptor<Object> typeDescriptor = new PojoInjectionDescriptor<>(type);
		if (typeDescriptor.canBeInstantiated()) {
			IPojoFactory<Object> factory = new PojoInjectionFactory<>(typeDescriptor, this);
			IPojoInitializer initializer = IPojoInitializer.standard();
			return LazyPojo.forFactory(factory, initializer).getInstance();
		}
		
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object doResolve(IParameterType type, Class<?> rawType) {
		if (IBeanInjector.class.equals(rawType)) {
			return new BeanInjector(this);
		
		} else if (IQualifiedProvider.class.equals(rawType)) {
			return new QualifiedProvider(type.getFirstParamToken(), this);
		
		} else if (Provider.class.equals(rawType)) {
			ParameterTypeInjectionDescriptor firstParamType = new ParameterTypeInjectionDescriptor(
					type.getFirstParamToken().getType(), 
					(Annotation[]) type.getAnnotations().toArray(new Annotation[0]), 
					type.getDeclaringClass());
			return new LazyResolvingProvider<>(this, firstParamType);
		}
		
		Collection<Annotation> qualifiers = type.getQualifiers();
		if (!qualifiers.isEmpty()) {
			return doResolveQualifiers(type, rawType);
		}
		
		if (type.getNamed().isPresent()) { // find by name
			Object result = findByName((String) type.getNamed().get(), rawType);
			if (result != null) {
				return result;
			}
		
		} else { // find by type
			Object result = findByType(type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	protected Object doResolveCollection(Class<?> rawType, Class<?> paramClass) {
		Collection<?> providers = findAllByType(paramClass);
		Preconditions.checkNotNull(providers);
		
		if (rawType.isAssignableFrom(ImmutableList.class)) {
			return ImmutableList.copyOf(providers);
		
		} else if (rawType.isAssignableFrom(ImmutableSet.class)) {
			return ImmutableSet.copyOf(providers);
		
		} else if (rawType.isAssignableFrom(ImmutableSortedSet.class)) {
			return ImmutableSortedSet.copyOf(providers);
		
		} else {
			throw new RuntimeException(rawType + "is an unsupported collection type");
		}
	}
	
	protected Object doResolveQualifiers(IParameterType<?> type, Class<?> clazz) {
		Object result = null;
		
		for (Annotation qualifier : type.getQualifiers()) {
			Object query = findByQualifier(clazz, qualifier);
			
			if (result != null && !result.equals(query)) {
				return null;
			} else {
				result = query;
			}
		}
		
		return result;
	}
	
	
	protected abstract @Nullable Object findByName(String name, Class<?> type);
	protected abstract @Nullable Object findByType(IParameterType<?> type);
	protected abstract Collection<?> findAllByType(Class<?> type);

	protected abstract @Nullable Object findByQualifier(Class<?> type, Annotation qualifier);
	
	protected boolean isCollection(IParameterType<?> type) {
		return Iterable.class.isAssignableFrom(type.getRawType());
	}
	
}

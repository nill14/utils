package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IQualifiedProvider;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

@SuppressWarnings("serial")
public abstract class AbstractPropertyResolver implements IPropertyResolver {
	
	@Override
	public Object resolve(Object pojo, IParameterType type) {
		Class<?> rawType = type.getRawType();
		
		boolean isCollection = isCollection(type);
		if (isCollection || type.isOptional()) { 
			Class<?> paramClass = type.getFirstParamToken().getRawType();

			if (java.util.Optional.class.isAssignableFrom(rawType)) {
				return java.util.Optional.ofNullable(doResolve(pojo, type, paramClass));
			}
			
			if (com.google.common.base.Optional.class.isAssignableFrom(rawType)) {
				return com.google.common.base.Optional.fromNullable(doResolve(pojo, type, paramClass));
			}
			
			if (Iterable.class.isAssignableFrom(rawType)) {
				return doResolveCollection(pojo, rawType, paramClass);
			}
		} 
	
		Object result = doResolve(pojo, type, rawType);
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
	protected Object doResolve(Object pojo, IParameterType type, Class<?> rawType) {
		if (IBeanInjector.class.equals(rawType)) {
			return new BeanInjector(this);
		
		} else if (IQualifiedProvider.class.equals(rawType)) {
			return new QualifiedProvider(type.getFirstParamToken(), this);
		}
		
		Collection<Annotation> qualifiers = type.getQualifiers();
		if (!qualifiers.isEmpty()) {
			return doResolveQualifiers(pojo, type, rawType);
		}
		
		if (type.getNamed().isPresent()) { // find by name
			Object result = findByName(pojo, type.getNamed().get(), rawType);
			if (result != null) {
				return result;
			}
		
		} else { // find by type
			Object result = findByType(pojo, type, rawType);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	protected Object doResolveCollection(Object pojo, Class<?> rawType, Class<?> paramClass) {
		Collection<?> providers = findAllByType(pojo, paramClass);
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
	
	protected Object doResolveQualifiers(Object pojo, IParameterType type, Class<?> clazz) {
		Object result = null;
		
		for (Annotation qualifier : type.getQualifiers()) {
			Object query = findByQualifier(pojo, clazz, qualifier);
			
			if (result != null && !result.equals(query)) {
				return null;
			} else {
				result = query;
			}
		}
		
		return result;
	}
	
	
	protected abstract Object findByName(Object pojo, String name, Class<?> type);
	protected abstract Object findByType(Object pojo, IParameterType type, Class<?> clazz);
	protected abstract Collection<?> findAllByType(Object pojo, Class<?> type);

	protected abstract Object findByQualifier(Object pojo, Class<?> type, Annotation qualifier);
	
	protected boolean isCollection(IParameterType type) {
		return Iterable.class.isAssignableFrom(type.getRawType());
	}
	
}

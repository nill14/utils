package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.inject.Provider;

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
	public Object resolve(Object pojo, IParameterType<?> type) {
		Class<?> rawType = type.getRawType();
		
		
		boolean isProvider = isProvider(type);
		boolean isCollection = isCollection(type);
		if (isCollection || isProvider || type.isOptional()) { 
			Class<?> paramClass = type.getFirstParamToken().getRawType();

			if (java.util.Optional.class.isAssignableFrom(rawType)) {
				return java.util.Optional.ofNullable(doResolve(pojo, type, paramClass)).map(Provider::get);
			}
			
			if (com.google.common.base.Optional.class.isAssignableFrom(rawType)) {
				Provider<?> opt = doResolve(pojo, type, paramClass);
				if (opt != null) {
					return com.google.common.base.Optional.of(opt.get());
				} else {
					return com.google.common.base.Optional.absent();
				}
			}
			
			if (isProvider) {
				return doResolve(pojo, IParameterType.of(type.getFirstParamToken()), paramClass);
			}

			if (Iterable.class.isAssignableFrom(rawType)) {
				return doResolveCollection(pojo, rawType, paramClass);
			}
		} 
	
		Provider<?> result = doResolve(pojo, type, rawType);
		if (result != null) {
			return result.get();
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
	protected Provider<?> doResolve(Object pojo, IParameterType type, Class<?> rawType) {
		if (IBeanInjector.class.equals(rawType)) {
			return new BeanInjector(this).toProvider();
		
		} else if (IQualifiedProvider.class.equals(rawType)) {
			return new QualifiedProvider(type.getFirstParamToken(), this).toProvider();
		
		} 
		
		Collection<Annotation> qualifiers = type.getQualifiers();
		if (!qualifiers.isEmpty()) {
			return doResolveQualifiers(pojo, type, rawType);
		}
		
		if (type.getNamed().isPresent()) { // find by name
			Provider<?> result = findByName(pojo, (String) type.getNamed().get(), rawType);
			if (result != null) {
				return result;
			}
		
		} else { // find by type
			Provider<?> result = findByType(pojo, type, rawType);
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
	
	protected Provider<?> doResolveQualifiers(Object pojo, IParameterType<?> type, Class<?> clazz) {
		Provider<?> result = null;
		
		for (Annotation qualifier : type.getQualifiers()) {
			Provider<?> query = findByQualifier(pojo, clazz, qualifier);
			
			if (result != null && !result.equals(query)) {
				return null;
			} else {
				result = query;
			}
		}
		
		return result;
	}
	
	
	protected abstract Provider<?> findByName(Object pojo, String name, Class<?> type);
	protected abstract Provider<?> findByType(Object pojo, IParameterType<?> type, Class<?> clazz);
	protected abstract Collection<?> findAllByType(Object pojo, Class<?> type);

	protected abstract Provider<?> findByQualifier(Object pojo, Class<?> type, Annotation qualifier);
	
	protected boolean isCollection(IParameterType<?> type) {
		return Iterable.class.isAssignableFrom(type.getRawType());
	}

	protected boolean isProvider(IParameterType<?> type) {
		return Provider.class.isAssignableFrom(type.getRawType());
	}
	
	protected Provider<?> provider(Object bean) {
		return new StaticProvider<>(bean);
	}
	
	private static class StaticProvider<T> implements Provider<T> {
		private final T bean;

		public StaticProvider(T bean) {
			Preconditions.checkNotNull(bean);
			this.bean = bean;
		}
		@Override
		public T get() {
			return bean;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof StaticProvider) {
				return bean.equals(((StaticProvider) obj).get());
			}
			return super.equals(obj);
		}
		
		@Override
		public int hashCode() {
			return bean.hashCode();
		}
	}
}

package com.github.nill14.utils.init.impl;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.api.IType;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * The ServiceRegistry must not be serializable. Serializing the registry indicates a programming error.
 *
 */
public class ServiceRegistry implements IServiceRegistry {
	
	private final ConcurrentHashMap<String, Object> beans = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Class<?>, LinkedBlockingQueue<Object>> services = new ConcurrentHashMap<>();
	private IPropertyResolver delegateResolver;
	
	/**
	 * 
	 * Not thread-safe
	 */
	public void setDelegateResolver(IPropertyResolver delegateResolver) {
		if(this.delegateResolver != null) {
			throw new IllegalStateException("can be set only once");
		}
		this.delegateResolver = delegateResolver;
	}
	
	private String generateName(Class<?> type) {
		return type.getTypeName();
	}
	
	@Override
	public <T> void addService(Class<T> serviceBean) {
		addService(generateName(serviceBean), serviceBean);
	}
	
	@Override
	public <S, T extends S> void addService(String name, Class<T> serviceBean) {
		ILazyPojo<T> lazyPojo = LazyPojo.forClass(serviceBean, annotationInitializer);
		Object proxy = LazyJdkProxy.newProxy(lazyPojo);
		Set<Class<?>> types = new PojoInjectionDescriptor(serviceBean).getDeclaredTypes();
		types.forEach((type) -> addElement(type, proxy));
		beans.put(name, proxy);
	}
	
	@Override
	public <S, F extends IPojoFactory<? extends S>> void addServiceFactory(
			Class<S> iface, Class<F> factoryBean) {
		addServiceFactory(iface, generateName(iface), factoryBean);
	}

	@Override
	public <S, F extends IPojoFactory<? extends S>> void addServiceFactory(
			Class<S> iface, String name, Class<F> factoryBean) {
		ILazyPojo<S> lazyPojo = LazyPojo.forFactory(iface, factoryBean, annotationInitializer);
		Object proxy = LazyJdkProxy.newProxy(lazyPojo);
		Set<Class<?>> types = new PojoInjectionDescriptor(iface).getDeclaredTypes();
		types.forEach((type) -> addElement(type, proxy));
		beans.put(name, proxy);
	}

	@Override
	public <S> S getService(Class<S> iface) {
		Optional<S> optional = getOptionalService(iface);
		return iface.cast(optional.get());
	}
	
	@Override
	public <S> S getService(Class<S> iface, String name) {
		S service = iface.cast(beans.get(name));
		Objects.requireNonNull(service);
		return service;
	}

	@Override
	public <S> Optional<S> getOptionalService(Class<S> iface) {
		return getServices(iface).stream().findFirst();
	}
	
	@Override
	public <S> Optional<S> getOptionalService(Class<S> iface, String name) {
		S service = iface.cast(beans.get(name));
		return Optional.ofNullable(service);
	}

	private void addElement(Class<?> registrable, Object proxy) {
		Queue<Object> queue = services.computeIfAbsent(registrable, r -> new LinkedBlockingQueue<>());
		queue.add(proxy);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> Collection<S> getServices(Class<S> registrable) {
		Queue<S> queue = (Queue<S>) services.get(registrable);
		if (queue != null) {
			return ImmutableList.copyOf(queue);
		
		} else {
			return ImmutableList.of();
		}
	}
	
	@Override
	public <T> void addSingleton(T serviceBean) {
		addSingleton(Objects.toString(serviceBean), serviceBean);
	}
	
	@Override
	public <T> void addSingleton(String name, T serviceBean) {
		Set<Class<?>> types = new PojoInjectionDescriptor(serviceBean.getClass()).getDeclaredTypes();
		types.forEach((type) -> addElement(type, serviceBean));
		beans.put(name, serviceBean);
	}
	
	public Collection<Class<?>> getBeans() {
		Builder<Class<?>> builder = ImmutableList.builder();
		builder.addAll(services.keySet());
//		builder.addAll(providers.values())
		return builder.build();
	}
	
	private final IPropertyResolver resolver = new IPropertyResolver() {
		
		private static final long serialVersionUID = 746185406164849945L;

		@Override
		public Object resolve(Object pojo, IType type) {
			
			if (delegateResolver != null) {
				Object resolve = delegateResolver.resolve(pojo, type);
				if (resolve != null) {
					return resolve;
				}
			}

			Optional<?> optionalNamed = getOptionalService(type.getRawType(), type.getName());
			if (optionalNamed.isPresent()) {
				return optionalNamed.get();
			}
			
			if (type.isParametrized()) {
				Class<?> rawType = type.getRawType();
				Class<?> paramClass = type.getFirstParamClass();

				if (Optional.class.isAssignableFrom(rawType)) {
					Optional<?> optional = getOptionalService(paramClass);
					return optional;
				}
				
				if (Collection.class.isAssignableFrom(rawType)) {
					Collection<?> providers = getServices(paramClass);
					
					if (Set.class.isAssignableFrom(rawType)) {
						return ImmutableSet.copyOf(providers);
					} else {
						return ImmutableList.copyOf(providers);
					}
				}
			}
			
			Optional<?> optionalTyped = getOptionalService(type.getRawType());
			if (optionalTyped.isPresent()) {
				return optionalTyped.get();
			}
			
			return null;
		}
	};
	
	
	public IPropertyResolver toResolver() {
		return resolver;
	}
	
	private final IPojoInitializer<Object> annotationInitializer = AnnotationPojoInitializer.withResolver(resolver);

}

package com.github.nill14.utils.init.impl;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceRegistry;

/**
 * 
 * The ServiceRegistry must not be serializable. Serializing the registry indicates a programming error.
 *
 */
public class ServiceRegistry implements IServiceRegistry {
	
	private final ConcurrentHashMap<Class<?>, Object> services = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Class<?>, LinkedBlockingQueue<Object>> providers = new ConcurrentHashMap<>();
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
	
	@Override
	public <S, T extends S> void putService(Class<S> iface, Class<T> serviceBean) {
		ILazyPojo<T> lazyPojo = LazyPojo.forClass(serviceBean, annotationInitializer);
		services.put(iface, LazyJdkProxy.newProxy(lazyPojo));
	}

	@Override
	public <S, F extends IPojoFactory<? extends S>> void putServiceFactory(
			Class<S> iface, Class<F> factoryBean) {
		ILazyPojo<S> lazyPojo = LazyPojo.forFactory(iface, factoryBean, annotationInitializer);
		services.put(iface, LazyJdkProxy.newProxy(lazyPojo));		
	}

	@Override
	public <S> S getService(Class<S> iface) {
		S service = iface.cast(services.get(iface));
		Objects.requireNonNull(service);
		return service;
	}

	@Override
	public <S> Optional<S> getOptionalService(Class<S> iface) {
		S service = iface.cast(services.get(iface));
		return Optional.ofNullable(service);
	}

	@Override
	public <S, T extends S> void addProvider(Class<S> registrable,
			Class<T> providerClass) {
		
		ILazyPojo<T> lazyPojo = LazyPojo.forClass(providerClass, annotationInitializer);
		Queue<Object> queue = providers.computeIfAbsent(registrable, r -> new LinkedBlockingQueue<>());
		queue.add(LazyJdkProxy.newProxy(lazyPojo));
	}

	@Override
	public <S, F extends IPojoFactory<? extends S>> void addProviderFactory(
			Class<S> registrable, Class<F> providerFactoryClass) {
		
		ILazyPojo<S> lazyPojo = LazyPojo.forFactory(registrable, providerFactoryClass, annotationInitializer);
		Queue<Object> queue = providers.computeIfAbsent(registrable, r -> new LinkedBlockingQueue<>());
		queue.add(LazyJdkProxy.newProxy(lazyPojo));		
	}

	@Override
	public <S> S[] getProviders(Class<S> registrable) {
		Queue<Object> queue = providers.get(registrable);
        @SuppressWarnings("unchecked")
		S[] arr = (S[]) Array.newInstance(registrable, queue.size());
		return queue.toArray(arr);
	}
	
	private final IPropertyResolver resolver = new IPropertyResolver() {
		
		private static final long serialVersionUID = 746185406164849945L;

		@Override
		public Object resolve(Object pojo, Class<?> propertyType,
				String propertyName) {
			
			if (delegateResolver != null) {
				Object resolve = delegateResolver.resolve(pojo, propertyType, propertyName);
				if (resolve != null) {
					return resolve;
				}
			}
			
			Optional<?> optional = getOptionalService(propertyType);
			if (optional.isPresent()) {
				return optional.get();
			}

			if (propertyType.isArray()) {
				Class<?> registrable = propertyType.getComponentType();
				return getProviders(registrable);
			}
			
			return null;
		}
	};
	
	private final IPojoInitializer<Object> annotationInitializer = AnnotationPojoInitializer.withResolver(resolver);

}

package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * 
 * The ServiceRegistry must not be serializable. Serializing the registry indicates a programming error.
 *
 */
public class ServiceRegistry implements IServiceRegistry {
	
	private final ConcurrentHashMap<String, Object> beans = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Class<?>, Map<String, Object>> services = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Class<?>, Map<Annotation, Object>> qualifiers = new ConcurrentHashMap<>();
	
	private final ChainingPropertyResolver resolver = new ChainingPropertyResolver(new ServiceRegistryPropertyResolver());
	
	/**
	 * 
	 * @param delegateResolver The extra resolver which can provide the answer first.
	 */
	public void pushDelegateResolver(IPropertyResolver delegateResolver) {
		resolver.pushResolver(delegateResolver);
	}
	
	private String generateName(Class<?> type) {
		Named named = type.getAnnotation(Named.class);
		if (named != null) {
			return named.value();
		
		} else {
			return type.getTypeName();
		}
	}
	
	@Override
	public <T> void addService(Class<T> serviceBean, IServiceContext context) {
		addService(generateName(serviceBean), serviceBean, context);
	}
	
	private <T> IPojoInitializer<Object> getInitializer(IServiceContext context) {
		IPojoInitializer<Object> annotationInitializer = this.annotationInitializer;
		
		Optional<IPropertyResolver> contextResolver = context.getCustomResolver();
		if (contextResolver.isPresent()) {
			annotationInitializer = AnnotationPojoInitializer.withResolver(contextResolver.get());
		}
		
		Optional<IPojoInitializer<Object>> contextInitializer = context.getInitializer();
		if (contextInitializer.isPresent()) {
			return new ChainingPojoInitializer()
					.addInitializer(contextInitializer.get())
					.addInitializer(annotationInitializer);
		} else {
			return annotationInitializer;
		}
		
	}
	
	private <T> Object newProxy(ILazyPojo<?> lazyPojo, Class<T> serviceBean) {
//		Object proxy = LazyJdkProxy.newProxy(lazyPojo);
		Object proxy = LazyJavassistProxy.newProxy(lazyPojo);
		return serviceBean.cast(proxy);
	}
	
	@Override
	public <S, T extends S> void addService(String name, Class<T> serviceBean, IServiceContext context) {
		
		IPojoInitializer<Object> initializer = getInitializer(context);
		ILazyPojo<T> lazyPojo = LazyPojo.forClass(serviceBean, initializer);
		
		Object proxy = newProxy(lazyPojo, serviceBean);
		PojoInjectionDescriptor pd = new PojoInjectionDescriptor(serviceBean);
		pd.getDeclaredTypes().forEach((type) -> addElement(type, name, proxy, pd.getDeclaredQualifiers()));
		Object old = beans.put(name, proxy);
		Preconditions.checkArgument(old == null, "Duplicate bean " + old);
	}
	
	@Override
	public <S, F extends IPojoFactory<? extends S>> void addServiceFactory(
			Class<S> iface, Class<F> factoryBean, IServiceContext context) {
		addServiceFactory(iface, generateName(iface), factoryBean, context);
	}

	@Override
	public <S, F extends IPojoFactory<? extends S>> void addServiceFactory(
			Class<S> iface, String name, Class<F> factoryBean, IServiceContext context) {
		
		IPojoInitializer<Object> initializer = getInitializer(context);
		ILazyPojo<S> lazyPojo = LazyPojo.forFactory(iface, factoryBean, initializer);
		Object proxy = newProxy(lazyPojo, factoryBean);
		
		PojoInjectionDescriptor pd = new PojoInjectionDescriptor(iface);
		pd.getDeclaredTypes().forEach((type) -> addElement(type, name, proxy, pd.getDeclaredQualifiers()));
		
		beans.put(name, proxy);
	}

	@Override
	public <S> S getService(Class<S> iface) {
		Optional<S> optional = getOptionalService(iface);
		Preconditions.checkArgument(optional.isPresent(), String.format("Missing %s", iface));
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
		Object bean = beans.get(name);
		if (bean != null && iface.isAssignableFrom(bean.getClass())) {
			return Optional.of(iface.cast(bean));
		}
		return Optional.empty();
	}

	private void addElement(Class<?> registrable, String name, Object proxy, Set<Annotation> qualifiers) {
		Map<String, Object> s = services.computeIfAbsent(registrable, r -> new ConcurrentHashMap<>());
		s.put(name, proxy);
		
		Map<Annotation, Object> q = this.qualifiers.computeIfAbsent(registrable, r -> new ConcurrentHashMap<>());
		for (Annotation qualifier : qualifiers) {
			Object prev = q.put(qualifier, proxy);
			Preconditions.checkArgument(prev == null, 
					String.format("Duplicate qualifier %s for type %s", qualifier, registrable));
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <S> Collection<S> getServices(Class<S> registrable) {
		Preconditions.checkNotNull(registrable);
		Map<String, S> map = (Map<String, S>) services.getOrDefault(registrable, Collections.emptyMap());
		if (map != null) {
			return ImmutableList.copyOf(map.values());
		
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
		PojoInjectionDescriptor pd = new PojoInjectionDescriptor(serviceBean.getClass());
		pd.getDeclaredTypes().forEach((type) -> addElement(type, name, serviceBean, pd.getDeclaredQualifiers()));
		beans.put(name, serviceBean);
	}
	
	public Collection<Class<?>> getBeans() {
		Builder<Class<?>> builder = ImmutableList.builder();
		builder.addAll(services.keySet());
//		builder.addAll(providers.values())
		return builder.build();
	}
	
	public Collection<String> getBeanNames() {
		return beans.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public <T> Map<String, T> getBeansOfType(Class<T> type) {
		return (Map<String, T>) services.getOrDefault(type, Collections.emptyMap());
	}
	
	public Object getBean(String name) {
		return beans.get(name);
	}
	
	public IPropertyResolver toResolver() {
		return resolver;
	}
	
	public IBeanInjector toBeanInjector() {
		return new BeanInjector(toResolver(), annotationInitializer);
	}
	
	@SuppressWarnings("serial")
	private class ServiceRegistryPropertyResolver extends AbstractPropertyResolver {

		@Override
		protected Object findByQualifier(Object pojo, Class<?> type, Annotation qualifier) {
			return qualifiers.getOrDefault(type, Collections.emptyMap()).get(qualifier);
		}
		
		@Override
		protected Object findByName(Object pojo, String name, Class<?> type) {
			return getOptionalService(type, name).orElse(null);
		}

		@Override
		protected Object findByType(Object pojo, Class<?> type) {
			return getOptionalService(type).orElse(null);
		}

		@Override
		protected Collection<?> findAllByType(Object pojo, Class<?> type) {
			return getServices(type);
		}

	};
	
	private final IPojoInitializer<Object> annotationInitializer = new ChainingPojoInitializer()
			.addInitializer(AnnotationPojoInitializer.withResolver(resolver))
			.addInitializer(EventBusPojoInitializer.withResolver(resolver));

}

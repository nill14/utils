package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * The ServiceRegistry must not be serializable. Serializing the registry indicates a programming error.
 *
 */
public class ServiceRegistry implements IServiceRegistry {
	
	private final ConcurrentHashMap<String, ILazyPojo<?>> beans = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Class<?>, Map<String, ILazyPojo<?>>> services = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Class<?>, Map<Annotation, ILazyPojo<?>>> qualifiers = new ConcurrentHashMap<>();
	
	private final IPropertyResolver resolver = new ServiceRegistryPropertyResolver();
	private final ChainingPojoInitializer<Object> pojoInitializer = ChainingPojoInitializer.defaultInitializer();
	
	public ServiceRegistry() {
	}
	
	private String generateGlobalName(Class<?> type) {
		Named named = type.getAnnotation(Named.class);
		if (named != null) {
			return type.getTypeName() + "$" + named.value();
		
		} else {
			return type.getTypeName();
		}
	}
	
	@Override
	public <T> void addService(Class<T> serviceBean, IServiceContext context) {
		addService(generateGlobalName(serviceBean), serviceBean, context);
	}
	
	private <T> IPojoInitializer<Object> getInitializer(IServiceContext context) {
		Optional<IPojoInitializer<Object>> contextInitializer = context.getInitializer();
		if (contextInitializer.isPresent()) {
			return pojoInitializer.with(contextInitializer.get());
		} else {
			return pojoInitializer;
		}
	}
	
	private <T> IPropertyResolver getResolver(IServiceContext context) {
		Optional<IPropertyResolver> customResolver = context.getCustomResolver();
		if (customResolver.isPresent()) {
			return new ChainingPropertyResolver(customResolver.get(), resolver);
		} else {
			return resolver;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> ILazyPojo<T> newProxy(ILazyPojo<?> lazyPojo, Class<T> serviceBean) {
//		Object proxy = LazyJdkProxy.newProxy(lazyPojo);
//		Object proxy = LazyJavassistProxy.newProxy(lazyPojo);
//		return serviceBean.cast(proxy);
		return (ILazyPojo<T>) lazyPojo;
	}
	
	@Override
	public <S, T extends S> void addService(String name, Class<T> serviceBean, IServiceContext context) {
		
		IPojoInitializer<Object> initializer = getInitializer(context);
		IPropertyResolver resolver = getResolver(context);
		ILazyPojo<T> lazyPojo = LazyPojo.forClass(serviceBean, resolver, initializer);
		
		ILazyPojo<T> proxy = newProxy(lazyPojo, serviceBean);
		IBeanDescriptor<T> pd = new PojoInjectionDescriptor<>(serviceBean);
		pd.getDeclaredTypes().forEach((type) -> addElement(type, name, proxy, pd.getDeclaredQualifiers()));
		Object old = beans.put(name, proxy);
		Preconditions.checkArgument(old == null, "Duplicate bean " + old);
	}
	
	@Override
	public <S, F extends Provider<? extends S>> void addServiceFactory(
			Class<S> iface, Class<F> factoryBean, IServiceContext context) {
		addServiceFactory(iface, generateGlobalName(iface), factoryBean, context);
	}

	@Override
	public <S, F extends Provider<? extends S>> void addServiceFactory(
			Class<S> iface, String name, Class<F> factoryBean, IServiceContext context) {
		
		IPojoInitializer<Object> initializer = getInitializer(context);
		IPropertyResolver resolver = getResolver(context);
		ILazyPojo<S> lazyPojo = LazyPojo.forFactory(iface, factoryBean, resolver, initializer);
		ILazyPojo<F> proxy = newProxy(lazyPojo, factoryBean);
		
		IBeanDescriptor<S> pd = new PojoInjectionDescriptor<>(iface);
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
		Optional<ILazyPojo<S>> first = getServiceMap(iface).values().stream().findFirst();
		return first.map(ILazyPojo::getInstance);
	}
	
	@Override
	public <S> Optional<S> getOptionalService(Class<S> iface, String name) {
		Object bean = beans.get(name);
		if (bean != null && iface.isAssignableFrom(bean.getClass())) {
			return Optional.of(iface.cast(bean));
		}
		return Optional.empty();
	}

	private void addElement(Class<?> registrable, String name, ILazyPojo<?> proxy, Set<Annotation> qualifiers) {
		Map<String, ILazyPojo<?>> s = services.computeIfAbsent(registrable, r -> new ConcurrentHashMap<>());
		s.put(name, proxy);
		
		Map<Annotation, ILazyPojo<?>> q = this.qualifiers.computeIfAbsent(registrable, r -> new ConcurrentHashMap<>());
		for (Annotation qualifier : qualifiers) {
			Object prev = q.put(qualifier, proxy);
			Preconditions.checkArgument(prev == null, 
					String.format("Duplicate qualifier %s for type %s", qualifier, registrable));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <S> Map<String, ILazyPojo<S>> getServiceMap(Class<S> registrable) {
		Preconditions.checkNotNull(registrable);
		Map map = services.getOrDefault(registrable, Collections.emptyMap());
		if (map != null) {
			return map;
		
		} else {
			return ImmutableMap.of();
		}
	}
	
	@Override
	public <S> Collection<S> getServices(Class<S> registrable) {
		Map<String, ILazyPojo<S>> map = getServiceMap(registrable);
		if (map != null) {
			return ImmutableList.copyOf(map.values().stream().map(ILazyPojo::getInstance).iterator());
		
		} else {
			return ImmutableList.of();
		}
	}
	
	@Override
	public <T> void addSingleton(T serviceBean) {
		addSingleton(Objects.toString(serviceBean), serviceBean);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> void addSingleton(String name, T serviceBean) {
		ILazyPojo<T> pojo = LazyPojo.forSingleton(serviceBean);
		IBeanDescriptor<T> pd = new PojoInjectionDescriptor<T>((Class<T>) serviceBean.getClass());
		pd.getDeclaredTypes().forEach((type) -> addElement(type, name, pojo, pd.getDeclaredQualifiers()));
		beans.put(name, pojo);
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
	
	public <T> Map<String, T> getBeansOfType(Class<T> type) {
		Map<String, ILazyPojo<T>> map = getServiceMap(type);
		return map.entrySet().stream().collect(
				Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getInstance()));
	}
	
	public Object getBean(String name) {
		ILazyPojo<?> pojo = beans.get(name);
		if (pojo != null) {
			return pojo.getInstance();
			
		} else {
			return null;
		}
	}
	
	@Override
	public IPropertyResolver toResolver() {
		return resolver;
	}
	
	@Override
	public IBeanInjector toBeanInjector() {
		return new BeanInjector(toResolver(), pojoInitializer);
	}
	
	@SuppressWarnings("serial")
	private class ServiceRegistryPropertyResolver extends AbstractPropertyResolver {

		@Override
		protected Object findByQualifier(Object pojo, Class<?> type, Annotation qualifier) {
			ILazyPojo<?> lazyPojo = qualifiers.getOrDefault(type, Collections.emptyMap()).get(qualifier);
			if (lazyPojo != null) {
				return lazyPojo.getInstance();
			} else {
				return null;
			}
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
	
	
	

}

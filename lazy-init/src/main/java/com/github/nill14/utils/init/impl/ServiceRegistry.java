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

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Qualifier;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * The ServiceRegistry must not be serializable. Serializing the registry indicates a programming error.
 *
 */
@Deprecated
public class ServiceRegistry implements IServiceRegistry {
	
	private final ConcurrentHashMap<String, ILazyPojo<?>> beans = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Class<?>, Map<String, ILazyPojo<?>>> services = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Class<?>, Map<Annotation, ILazyPojo<?>>> qualifiers = new ConcurrentHashMap<>();
	
	private final ServiceRegistryPropertyResolver resolver = new ServiceRegistryPropertyResolver();
	
	public ServiceRegistry() {
	}
	
	private String generateGlobalName(Class<?> type) { //TODO make sure the generated name doesn't conflict
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
	
	
	private <T> IPropertyResolver getResolver(IServiceContext context) {
		Optional<AbstractPropertyResolver> customResolver = context.getCustomResolver();
		if (customResolver.isPresent()) {
			ChainingPropertyResolver chainingPropertyResolver = new ChainingPropertyResolver();
			chainingPropertyResolver.append(customResolver.get());
			chainingPropertyResolver.append(resolver);
			return chainingPropertyResolver;
		} else {
			return resolver;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> ILazyPojo<T> newProxy(ILazyPojo<?> lazyPojo, Class<T> serviceBean) {
//		Object proxy = LazyJdkProxy.newProxy(lazyPojo);
//		Object proxy = LazyJavassistProxy.newProxy(lazyPojo);
//		return (ILazyPojo<T>) LazyPojo.forSingleton(proxy, IPropertyResolver.empty());
		return (ILazyPojo<T>) lazyPojo;
	}
	
	@Override
	public <S, T extends S> void addService(String name, Class<T> serviceBean, IServiceContext context) {
		
		IPropertyResolver resolver = getResolver(context);
		ILazyPojo<T> lazyPojo = LazyPojo.forBean(serviceBean, resolver);
		
		ILazyPojo<T> proxy = newProxy(lazyPojo, serviceBean);
		IBeanDescriptor<T> pd = new PojoInjectionDescriptor<>(serviceBean);
		pd.getDeclaredTypes().forEach((type) -> addElement(type, name, proxy, getTypeQualifier(pd.getRawType())));
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
		
		IPropertyResolver resolver = getResolver(context);
		ILazyPojo<S> lazyPojo = LazyPojo.forProvider(factoryBean, resolver);
		ILazyPojo<F> proxy = newProxy(lazyPojo, factoryBean);
		
		IBeanDescriptor<S> pd = new PojoInjectionDescriptor<>(iface);
		pd.getDeclaredTypes().forEach((type) -> addElement(type, name, proxy, getTypeQualifier(pd.getRawType())));
		
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
		ILazyPojo<?> bean = beans.get(name);
		if (bean != null && iface.isAssignableFrom(bean.getType().getRawType())) {
			return Optional.of(iface.cast(bean.getInstance()));
		}
		return Optional.empty();
	}

	private void addElement(Class<?> registrable, String name, ILazyPojo<?> proxy, @Nullable Annotation qualifier) {
		Map<String, ILazyPojo<?>> s = services.computeIfAbsent(registrable, r -> new ConcurrentHashMap<>());
		s.put(name, proxy);
		
		if (qualifier != null) {
			Map<Annotation, ILazyPojo<?>> q = this.qualifiers.computeIfAbsent(registrable, r -> new ConcurrentHashMap<>());
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
		ILazyPojo<T> pojo = LazyPojo.forSingleton(serviceBean, IPropertyResolver.empty());
		IBeanDescriptor<T> pd = new PojoInjectionDescriptor<T>((Class<T>) serviceBean.getClass());
		pd.getDeclaredTypes().forEach((type) -> addElement(type, name, pojo, getTypeQualifier(pd.getRawType())));
		beans.put(name, pojo);
	}
	

	public void addBinding(BindingKey<?> BindingKey, ILazyPojo<?> lazyPojo) {
		String globalName = generateGlobalName(lazyPojo.getType().getRawType());
		addElement(BindingKey.getRawType(), globalName, lazyPojo, BindingKey.getQualifier());
		Object old = beans.put(globalName, lazyPojo);
		Preconditions.checkArgument(old == null, "Duplicate bean " + old);
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
	public IBeanInjector toBeanInjector(CallerContext context) {
		return new BeanInjector(toResolver(), context);
	}
	
	@SuppressWarnings("serial")
	private class ServiceRegistryPropertyResolver extends AbstractPropertyResolver {
		
		public ServiceRegistryPropertyResolver() {
			super();
		}

		@Override
		protected Object findByQualifier(IParameterType type, Annotation qualifier, CallerContext context) {
			ILazyPojo<?> lazyPojo = qualifiers.getOrDefault(type.getRawType(), Collections.emptyMap()).get(qualifier);
			if (lazyPojo != null) {
				return lazyPojo.getInstance();
			} else {
				return null;
			}
		}
		
		@Override
		protected Object findByName(String name, IParameterType type, CallerContext context) {
			ILazyPojo<?> bean = beans.get(name);
			if (bean != null && type.getToken().isAssignableFrom(bean.getType())) {
				return bean.getInstance();
			}
			return null;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected Object findByType(IParameterType type, CallerContext context) {
			Class<?> clazz = type.getRawType();
			Map<String, ILazyPojo<?>> serviceMap = getServiceMap((Class) clazz);
			Optional<ILazyPojo<?>> first = serviceMap.values().stream().findFirst();
			return first.map(x -> x.getInstance()).orElse(null);
		}

		@Override
		protected Collection<?> findAllByType(IParameterType type, CallerContext context) {
			return getServices(type.getRawType());
		}

	};
	
	private Annotation getTypeQualifier(Class<?> clazz) {
		return AnnotationScanner.findQualifier(clazz).orElse(null);
	}

	

}

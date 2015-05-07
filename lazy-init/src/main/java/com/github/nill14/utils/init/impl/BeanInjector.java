package com.github.nill14.utils.init.impl;

import javax.inject.Provider;


import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.reflect.TypeToken;

@SuppressWarnings("unchecked")
public class BeanInjector implements IBeanInjector {
	
	private final IPropertyResolver resolver;
	private final IPojoInitializer initializer;

	public BeanInjector(IPropertyResolver resolver) {
		this.resolver = resolver;
		this.initializer = IPojoInitializer.standard();
	}

	public BeanInjector(IPropertyResolver resolver, IPojoInitializer initializer) {
		this.resolver = resolver;
		this.initializer = initializer;
	}
	
	public void wire(Object bean) {
		IPojoFactory<Object> pojoFactory = PojoProviderFactory.singleton(bean, resolver);
		initializer.init(null, pojoFactory, bean);
	}

	@Override
	public void injectMembers(Object bean) {
		IPojoFactory<Object> pojoFactory = PojoProviderFactory.singleton(bean, resolver);
		initializer.init(null, pojoFactory, bean);
	}

	private <T> T resolve(BindingKey<T> type) {
		IParameterType parameterType = IParameterType.of(type);
		T bean = (T) resolver.resolve(parameterType);
		if (bean == null) {
			throw new RuntimeException(String.format(
					"Injection of bean %s failed!", type));
		}
		return bean;
	}

	@Override
	public <T> T wire(Class<T> beanClass) {
		return resolve(BindingKey.of(beanClass));
	}

	@Override
	public <T> T wire(TypeToken<T> typeToken) {
		return resolve(BindingKey.of(typeToken));
	}
	
	@Override
	public <T> T wire(BindingKey<T> type) {
		return resolve(type);
	}
	
	@Override
	public <T> T getInstance(Class<T> beanClass) {
		return resolve(BindingKey.of(beanClass));
	}

	@Override
	public <T> T getInstance(TypeToken<T> typeToken) {
		return resolve(BindingKey.of(typeToken));
	}
	
	@Override
	public <T> T getInstance(BindingKey<T> type) {
		return resolve(type);
	}
	
	private final Provider<BeanInjector> provider = new Provider<BeanInjector>() {
		
		@Override
		public BeanInjector get() {
			return BeanInjector.this;
		}
	};
	
	public Provider<BeanInjector> toProvider() {
		return provider;
	}


	@Override
	public <T> Provider<T> getProvider(Class<T> beanClass) {
		IParameterType type = IParameterType.of(BindingKey.of(beanClass));
		return new LazyResolvingProvider<>(resolver, type);
	}

	@Override
	public <T> Provider<T> getProvider(TypeToken<T> typeToken) {
		IParameterType type = IParameterType.of(BindingKey.of(typeToken));
		return new LazyResolvingProvider<>(resolver, type);
	}

	@Override
	public <T> Provider<T> getProvider(BindingKey<T> BindingKey) {
		IParameterType type = IParameterType.of(BindingKey);
		return new LazyResolvingProvider<>(resolver, type);
	}


	
}

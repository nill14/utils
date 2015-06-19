package com.github.nill14.utils.init.impl;

import java.io.Serializable;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.ICallerContext;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.google.common.reflect.TypeToken;

@SuppressWarnings({ "unchecked", "serial" })
public class BeanInjector implements IBeanInjector {
	
	private final IPropertyResolver resolver;

	public BeanInjector(IPropertyResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void injectMembers(Object bean) {
		IPojoFactory<Object> pojoFactory = BeanInstancePojoFactory.singleton(bean);
		resolver.initializeBean(pojoFactory.getDescriptor(), bean, ICallerContext.prototype());
	}

	private <T> T resolve(BindingKey<T> type) {
		IParameterType parameterType = IParameterType.of(type);
		T bean = (T) resolver.resolve(parameterType, ICallerContext.prototype());
		if (bean == null) {
			throw new RuntimeException(String.format(
					"Injection of bean %s failed!", type));
		}
		return bean;
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
		return new LazyResolvingProvider<>(resolver, type, ICallerContext.prototype());
	}

	@Override
	public <T> Provider<T> getProvider(TypeToken<T> typeToken) {
		IParameterType type = IParameterType.of(BindingKey.of(typeToken));
		return new LazyResolvingProvider<>(resolver, type, ICallerContext.prototype());
	}

	@Override
	public <T> Provider<T> getProvider(BindingKey<T> BindingKey) {
		IParameterType type = IParameterType.of(BindingKey);
		return new LazyResolvingProvider<>(resolver, type, ICallerContext.prototype());
	}


	
}

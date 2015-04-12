package com.github.nill14.utils.init.impl;

import java.util.Optional;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.schema.BeanProperty;
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
	
	@Override
	public <T> T wire(Class<T> beanClass) {
		IParameterType<T> type = ParameterTypeBuilder.builder(beanClass).build();
		return resolve(type);
	}

	private <T> T resolve(IParameterType<T> type) {
		T bean = (T) resolver.resolve(null, type).get();
		if (bean == null) {
			throw new RuntimeException(String.format(
					"Injection of bean %s failed!", type));
		}
		return bean;
	}

	@Override
	public <T> T wire(TypeToken<T> typeToken) {
		IParameterType<T> type = ParameterTypeBuilder.builder(typeToken).build();
		return resolve(type);
	}
	
	@Override
	public <T> T wire(IParameterType<T> type) {
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


	
}

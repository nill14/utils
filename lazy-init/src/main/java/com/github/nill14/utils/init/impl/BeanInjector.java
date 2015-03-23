package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.reflect.TypeToken;

@SuppressWarnings("unchecked")
public class BeanInjector implements IBeanInjector {
	
	private final IPropertyResolver resolver;
	private final IPojoInitializer<Object> initializer;

	public BeanInjector(IPropertyResolver resolver) {
		this.resolver = resolver;
		this.initializer = IPojoInitializer.standard();
	}

	public BeanInjector(IPropertyResolver resolver, IPojoInitializer<Object> initializer) {
		this.resolver = resolver;
		this.initializer = initializer;
	}
	
	public void wire(Object bean) {
		initializer.init(null, bean);
	}

	@Override
	public void injectMembers(Object bean) {
		initializer.init(null, bean);
	}
	
	@Override
	public <T> T wire(Class<T> beanClass) {
		IParameterType type = ParameterTypeBuilder.builder(beanClass).build();
		return (T) resolver.resolve(null, type);
	}

	@Override
	public <T> T wire(TypeToken<T> typeToken) {
		IParameterType type = ParameterTypeBuilder.builder(typeToken).build();
		return (T) resolver.resolve(null, type);
	}
	
	@Override
	public <T> T wire(IParameterType type) {
		return (T) resolver.resolve(null, type);
	}
	
}

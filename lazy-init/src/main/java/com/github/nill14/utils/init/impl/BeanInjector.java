package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.reflect.TypeToken;

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
		return wire(TypeToken.of(beanClass));
	}

	@Override
	public <T> T wire(TypeToken<T> typeToken) {
		IBeanDescriptor<T> typeDescriptor = new PojoInjectionDescriptor<>(typeToken);
		Provider<T> factory = new PojoFactory<>(typeDescriptor, resolver);
		return new LazyPojo<>(factory, typeDescriptor, initializer).getInstance();
	}
	
}

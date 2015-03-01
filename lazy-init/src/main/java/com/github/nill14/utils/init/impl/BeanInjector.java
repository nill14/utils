package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class BeanInjector implements IBeanInjector {
	
	private final IPropertyResolver resolver;
	private final IPojoInitializer<Object> initializer;

	public BeanInjector(IPropertyResolver resolver) {
		this.resolver = resolver;
		this.initializer = AnnotationPojoInitializer.withResolver(resolver);
	}

	public BeanInjector(IPropertyResolver resolver, IPojoInitializer<Object> initializer) {
		this.resolver = resolver;
		this.initializer = initializer;
	}
	
	@Override
	public void wire(Object bean) {
		initializer.init(null, bean);
	}

	@Override
	public <T> T wire(Class<T> beanClass) {
		return LazyPojo.forClass(beanClass, initializer).getInstance();
	}

}

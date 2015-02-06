package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class BeanInjector implements IBeanInjector {
	
	private final IPropertyResolver resolver;

	public BeanInjector(IPropertyResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void wire(Object bean) {
		AnnotationPojoInitializer initializer = AnnotationPojoInitializer.withResolver(resolver);
		initializer.init(null, bean);
	}

	@Override
	public <T> T wire(Class<T> beanClass) {
		AnnotationPojoInitializer initializer = AnnotationPojoInitializer.withResolver(resolver);
		return LazyPojo.forClass(beanClass, initializer).getInstance();
	}

}

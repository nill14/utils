package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IBeanInjector;

public class ServiceRegistryBeanInjector implements IBeanInjector {
	
	private final ServiceRegistry serviceRegistry;

	public ServiceRegistryBeanInjector(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void wire(Object bean) {
		AnnotationPojoInitializer initializer = AnnotationPojoInitializer.withResolver(serviceRegistry.toResolver());
		initializer.init(null, bean);
	}

	@Override
	public <T> T wire(Class<T> beanClass) {
		AnnotationPojoInitializer initializer = AnnotationPojoInitializer.withResolver(serviceRegistry.toResolver());
		return LazyPojo.forClass(beanClass, initializer).getInstance();
	}

}

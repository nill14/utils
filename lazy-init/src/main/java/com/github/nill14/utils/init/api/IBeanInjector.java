package com.github.nill14.utils.init.api;

/**
 * 
 * Similar as AutowireCapableBeanFactory.autowireBeanProperties(existingBean, autowireMode, dependencyCheck);
 *
 */
public interface IBeanInjector {
	
	void wire(Object bean);
	
	<T> T wire(Class<T> beanClass);
}

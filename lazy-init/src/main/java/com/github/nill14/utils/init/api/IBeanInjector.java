package com.github.nill14.utils.init.api;

import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;

/**
 * 
 * Similar as AutowireCapableBeanFactory.autowireBeanProperties(existingBean, autowireMode, dependencyCheck);
 * or {@link Injector#injectMembers(Object)}
 *
 */
public interface IBeanInjector {
	
	void injectMembers(Object bean);
	
	<T> T wire(Class<T> beanClass);
	
	<T> T wire(TypeToken<T> typeToken);
}

package com.github.nill14.utils.init.api;

import com.google.common.reflect.TypeToken;

/**
 * 
 * Similar as AutowireCapableBeanFactory.autowireBeanProperties(existingBean, autowireMode, dependencyCheck);
 * or {@link com.google.inject.Injector#injectMembers(Object)}
 *
 */
public interface IBeanInjector {
	
	void injectMembers(Object bean);
	
	<T> T wire(Class<T> beanClass);

	<T> T wire(TypeToken<T> typeToken);

	<T> T wire(IParameterType<T> type);
	
//	<T> T getInstance(Class<T> beanClass);
//	
//	<T> T getInstance(TypeToken<T> typeToken);
//	
//	<T> T getInstance(IParameterType<T> type);
//	
//	<T> Provider<T> getProvider(Class<T> beanClass);
//	
//	<T> Provider<T> getProvider(TypeToken<T> typeToken);
//	
//	<T> Provider<T> getProvider(IParameterType<T> type);
//	
//	<T> Optional<T> getOptionalInstance(Class<T> beanClass);
//	
//	<T> Optional<T> getOptionalInstance(TypeToken<T> typeToken);
//	
//	<T> Optional<T> getOptionalInstance(IParameterType<T> type);
}

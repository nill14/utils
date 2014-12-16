package com.github.nill14.utils.init.api;

import java.util.Optional;

public interface IServiceRegistry {

	<S, T extends S> void putService(Class<S> iface, Class<T> serviceBean);
	
	<S, F extends IPojoFactory<? extends S>> void putServiceFactory(Class<S> iface, Class<F> factoryBean);

	<S> S getService(Class<S> iface);

	<S> Optional<S> getOptionalService(Class<S> iface);
	
	
	<S, T extends S> void addProvider(Class<S> registrable, Class<T> providerClass);
	
	<S, F extends IPojoFactory<? extends S>> void addProviderFactory(Class<S> registrable, Class<F> providerFactoryClass);
	
	<S> S[] getProviders(Class<S> registrable);

}

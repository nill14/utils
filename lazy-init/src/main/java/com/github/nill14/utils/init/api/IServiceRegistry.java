package com.github.nill14.utils.init.api;

import java.util.Collection;
import java.util.Optional;

import javax.inject.Provider;

import com.github.nill14.utils.init.impl.ServiceRegistry;

@Deprecated
public interface IServiceRegistry {

	<T> void addSingleton(T serviceBean);
	
	<T> void addSingleton(String name, T serviceBean);
	
	<T> void addService(Class<T> serviceBean, IServiceContext context);
	
	<S, T extends S> void addService(String name, Class<T> serviceBean, IServiceContext context);
	
	<S, F extends Provider<? extends S>> void addServiceFactory(
			Class<S> iface, String name, Class<F> factoryBean, IServiceContext context);
	
	<S, F extends Provider<? extends S>> void addServiceFactory(
			Class<S> iface, Class<F> factoryBean, IServiceContext context);
	
	<S> S getService(Class<S> iface);

	<S> Optional<S> getOptionalService(Class<S> iface);
	
	<S> S getService(Class<S> iface, String name);

	<S> Optional<S> getOptionalService(Class<S> iface, String name);
	
	<S> Collection<S> getServices(Class<S> registrable);

	IPropertyResolver toResolver();

	IBeanInjector toBeanInjector(ICallerContext context);


	static IServiceRegistry newRegistry() {
		return new ServiceRegistry();
	}

}

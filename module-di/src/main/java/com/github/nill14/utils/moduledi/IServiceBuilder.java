package com.github.nill14.utils.moduledi;

import java.util.Collection;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IServiceRegistry;

public interface IServiceBuilder {

	
	IServiceBuilder buildServices(IServiceRegistry registry);

	<S, T extends S> IServiceBuilder addBean(Class<T> impl, Class<S> iface);

	IServiceBuilder buildDependencies(IDependencyDescriptorBuilder<Class<?>> dependencyBuilder);

	Collection<Class<?>> getBeans();
	
}

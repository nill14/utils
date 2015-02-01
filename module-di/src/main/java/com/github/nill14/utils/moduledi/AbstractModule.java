package com.github.nill14.utils.moduledi;

import com.github.nill14.parsers.dependency.IDependencyDescriptor;
import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.parsers.dependency.impl.DependencyDescriptor;
import com.github.nill14.utils.init.api.IServiceRegistry;

public abstract class AbstractModule extends com.google.inject.AbstractModule {

	private IDependencyDescriptor<Class<?>> dependencyDescriptor;
	
	
	public IDependencyDescriptor<Class<?>> getDependencyDescriptor() {
		return dependencyDescriptor;
	}
	
	public final void buildServices(IServiceRegistry registry) {
		IDependencyDescriptorBuilder<Class<?>> dependencyBuilder = DependencyDescriptor.builder(getClass());
		IServiceBuilder serviceBuilder = new ServiceBuilder(new ModuleServiceContext(this, registry));
		buildServices(serviceBuilder);
//		serviceBuilder.buildDependencies(dependencyBuilder);
		buildDependencies(dependencyBuilder);
		dependencyDescriptor = dependencyBuilder.build();
		serviceBuilder.buildServices(registry);
	}
	
	public void startModule(IServiceRegistry registry) {
		
	}
	
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {}
	
	public abstract void buildServices(IServiceBuilder builder);
	
	
	

	
}

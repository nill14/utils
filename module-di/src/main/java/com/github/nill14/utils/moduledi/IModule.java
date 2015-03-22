package com.github.nill14.utils.moduledi;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IServiceRegistry;

public interface IModule {

//	void buildServices(IServiceRegistry registry);
	
	void startModule(IServiceRegistry registry);
	
	void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder);
	
	void buildServices(IServiceBuilder builder);
	
	
	

	
}

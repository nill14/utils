package com.github.nill14.utils.moduledi2;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.Binder;

public interface IModule {

//	void buildServices(IServiceRegistry registry);
	
	void activate(IBeanInjector beanInjector);
	
	//TODO consider encapsulating due to exposing class as a building block - may change in future
	void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder); 
	
	void configure(Binder binder);
	
	
	

	
}

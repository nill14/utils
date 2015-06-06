package com.github.nill14.utils.moduledi;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.Binder;

public interface IModule {

//	void buildServices(IServiceRegistry registry);
	
	void startModule(IBeanInjector beanInjector);
	
	void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder);
	
	void buildServices(Binder binder);
	
	
	

	
}

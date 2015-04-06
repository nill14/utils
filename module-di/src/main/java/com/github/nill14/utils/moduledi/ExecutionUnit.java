package com.github.nill14.utils.moduledi;

import com.github.nill14.parsers.dependency.IDependencyDescriptor;
import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.parsers.dependency.impl.DependencyDescriptor;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.spring.SpringModuleServiceContext;
import com.github.nill14.utils.moduledi.IModule;

public class ExecutionUnit {

	
	private IDependencyDescriptor<Class<?>> dependencyDescriptor;
	private final IModule module;
	
	
	public ExecutionUnit(IModule module) {
		this.module = module;
	}
	
	public IDependencyDescriptor<Class<?>> getDependencyDescriptor() {
		return dependencyDescriptor;
	}
	
	public void buildServices(IServiceRegistry registry) {
		IDependencyDescriptorBuilder<Class<?>> dependencyBuilder = DependencyDescriptor.builder(module.getClass());
		IServiceBuilder serviceBuilder = new ServiceBuilder(createModuleContext(module, registry));
		module.buildServices(serviceBuilder);
		serviceBuilder.buildDependencies(dependencyBuilder);
		module.buildDependencies(dependencyBuilder);
		dependencyDescriptor = dependencyBuilder.build();
		serviceBuilder.registerServices(registry);
	}
	
	public void startModule(IServiceRegistry registry) {
		module.startModule(registry);
	}
	
	private IServiceContext createModuleContext(IModule module, IServiceRegistry registry) {
		if (SpringModuleServiceContext.isSupported(module)) {
			return new SpringModuleServiceContext(module, registry);
		
//		} else if (GuiceModuleServiceContext.isSupported(module)) {
//			return new GuiceModuleServiceContext(module, registry);
		
		} else {
			return IServiceContext.global();
		}
	}
	
	@Override
	public String toString() {
		return module.toString();
	}
	
	public String getName() {
		return module.getClass().getSimpleName();
	}
}

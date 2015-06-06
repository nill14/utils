package com.github.nill14.utils.moduledi;

import com.github.nill14.parsers.dependency.IDependencyDescriptor;
import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.parsers.dependency.impl.DependencyDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.binding.ModuleBinder;

public class ExecutionUnit {

	
	private IDependencyDescriptor<Class<?>> dependencyDescriptor;
	private final IModule module;
	
	
	public ExecutionUnit(IModule module) {
		this.module = module;
	}
	
	public IDependencyDescriptor<Class<?>> getDependencyDescriptor() {
		return dependencyDescriptor;
	}
	
	public void buildServices(ModuleBinder binder) {
		IDependencyDescriptorBuilder<Class<?>> dependencyBuilder = DependencyDescriptor.builder(module.getClass());
		createModuleContext(module);
		module.buildServices(binder);
//		serviceBuilder.buildDependencies(dependencyBuilder);
//		beans.values().stream().forEach(iface -> dependencyBuilder.provides(iface));
		module.buildDependencies(dependencyBuilder);
		dependencyDescriptor = dependencyBuilder.build();
//		serviceBuilder.registerServices(registry);
//		beans.forEach((serviceBean, service) -> 
//		serviceRegistry.addService(serviceBean, context));
	}
	
	public void startModule(IBeanInjector beanInjector) {
		module.startModule(beanInjector);
	}
	
	private IServiceContext createModuleContext(IModule module) {
//		if (SpringModuleServiceContext.isSupported(module)) {
//			return new SpringModuleServiceContext(module, registry);
		
//		} else if (GuiceModuleServiceContext.isSupported(module)) {
//			return new GuiceModuleServiceContext(module, registry);
		
//		} else {
			return IServiceContext.global();
//		}
	}
	
	@Override
	public String toString() {
		return module.toString();
	}
	
	public String getName() {
		return module.getClass().getSimpleName();
	}
}

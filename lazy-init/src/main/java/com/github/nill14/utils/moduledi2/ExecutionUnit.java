package com.github.nill14.utils.moduledi2;

import com.github.nill14.parsers.dependency.IDependencyDescriptor;
import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.parsers.dependency.impl.DependencyDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.Binder;

public class ExecutionUnit {

	
	private IDependencyDescriptor<Class<?>> dependencyDescriptor;
	private final IModule module;
	
	
	public ExecutionUnit(IModule module) {
		this.module = module;
	}
	
	public IDependencyDescriptor<Class<?>> getDependencyDescriptor() {
		return dependencyDescriptor;
	}
	
	public void configure(Binder binder) {
		IDependencyDescriptorBuilder<Class<?>> dependencyBuilder = DependencyDescriptor.builder(module.getClass());
		module.configure(binder);
//		serviceBuilder.buildDependencies(dependencyBuilder);
		module.buildDependencies(dependencyBuilder);
		dependencyDescriptor = dependencyBuilder.build();
//		serviceBuilder.registerServices(registry);
	}
	
	public void activate(IBeanInjector beanInjector) {
		module.activate(beanInjector);
	}
	
	@Override
	public String toString() {
		return module.toString();
	}
	
	public String getName() {
		return module.getClass().getSimpleName();
	}
}

package com.github.nill14.utils.moduledi.module;

import java.util.Optional;

import org.testng.Assert;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.Binder;
import com.github.nill14.utils.moduledi.IModule;
import com.github.nill14.utils.moduledi.bean.customer.ITaskService;

public class ActivationModule implements IModule {

	@Override
	public void buildServices(Binder binder) {
		
	}
	
	@Override
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
		builder.uses(CustomerModule.class);
	}
	

	@Override
	public void startModule(IBeanInjector beanInjector) {
		
		ITaskService taskService = beanInjector.getInstance(ITaskService.class);
		Assert.assertEquals(taskService.getReporter(), Optional.of("reporter"));
		Assert.assertEquals(taskService.getAssignee(), Optional.empty());
		
//		TaskBean taskBean = taskService.
//		Assert.assertEquals(Optional.empty(), taskBean.getAssignee());
//		Assert.assertEquals(Optional.of("reporter"), taskBean.getReporter());
	}
	
	
	
}

package com.github.nill14.utils.moduledi.module;

import java.util.Optional;

import org.junit.Assert;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.AbstractModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.bean.customer.ITaskService;

public class ActivationModule extends AbstractModule {

	@Override
	protected void configure() {
	}

	@Override
	public void buildServices(IServiceBuilder builder) {
		
	}
	
	@Override
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
		builder.uses(CustomerModule.class);
	}
	
	@Override
	public void startModule(IServiceRegistry registry) {
		
		ITaskService taskService = registry.getService(ITaskService.class);
		Assert.assertEquals(Optional.empty(), taskService.getAssignee());
		Assert.assertEquals(Optional.of("reporter"), taskService.getReporter());
		
//		TaskBean taskBean = taskService.
//		Assert.assertEquals(Optional.empty(), taskBean.getAssignee());
//		Assert.assertEquals(Optional.of("reporter"), taskBean.getReporter());
	}
	
	
	
}

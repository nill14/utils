package com.github.nill14.utils.moduledi.module;

import org.junit.Assert;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.IModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.service.IBreadService;
import com.github.nill14.utils.moduledi.service.ISnackService;
import com.github.nill14.utils.moduledi.service.SnackService;
import com.google.inject.AbstractModule;

public class SnackModule extends AbstractModule implements IModule {

	@Override
	protected void configure() {
		bind(ISnackService.class).to(SnackService.class);
	}

	@Override
	public void buildServices(IServiceBuilder builder) {
		builder.addBean(SnackService.class, ISnackService.class);
	}

	@Override
	public void startModule(IServiceRegistry registry) {
		IBreadService breadService = registry.getService(IBreadService.class);
		Assert.assertNotNull(breadService);
		
		ISnackService snackService = registry.getService(ISnackService.class);
		Assert.assertNotNull(snackService);
		Assert.assertNotNull(snackService.getBreadService());
	}

	@Override
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
		builder.usesOptionally(IBreadService.class);
	}
	
//	
//	@Override
//	protected void refreshContext(ClassPathXmlApplicationContext ctx, IServiceRegistry serviceRegistry) {
//		super.refreshContext(ctx, serviceRegistry);
//		System.out.println(ctx.getBean(Snack.class).getBread().getName());
//		System.out.println(ctx.getBean(Snack.class).getSnackService());
//	}
//	
//	@Override
//	public void startModule(IServiceRegistry registry) {
//		Assert.assertEquals(expected, actual);
//	}
}

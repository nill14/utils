package com.github.nill14.utils.moduledi.module;

import org.junit.Assert;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.IModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.service.BreadService;
import com.github.nill14.utils.moduledi.service.IBreadService;
import com.google.inject.AbstractModule;

public class BreadModule extends AbstractModule implements IModule {

	@Override
	protected void configure() {
		bind(IBreadService.class).to(BreadService.class);
	}

	@Override
	public void buildServices(IServiceBuilder builder) {
		builder.addBean(BreadService.class, IBreadService.class);
		
	}

	@Override
	public void startModule(IServiceRegistry registry) {
		IBreadService service = registry.getService(IBreadService.class);
		Assert.assertNotNull(service);
		
	}

	@Override
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

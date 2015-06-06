package com.github.nill14.utils.moduledi.module;

import org.testng.Assert;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.Binder;
import com.github.nill14.utils.moduledi.IModule;
import com.github.nill14.utils.moduledi.service.BreadService;
import com.github.nill14.utils.moduledi.service.IBreadService;
import com.google.inject.AbstractModule;

public class BreadModule extends AbstractModule implements IModule {

	@Override
	protected void configure() {
		bind(IBreadService.class).to(BreadService.class);
	}

	@Override
	public void buildServices(Binder binder) {
		binder.bind(IBreadService.class).to(BreadService.class);
		
	}


	
	@Override
	public void startModule(IBeanInjector beanInjector) {
		IBreadService service = beanInjector.getInstance(IBreadService.class);
		Assert.assertNotNull(service);
		
	}

	@Override
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

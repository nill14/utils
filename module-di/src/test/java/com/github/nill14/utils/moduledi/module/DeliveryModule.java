package com.github.nill14.utils.moduledi.module;

import org.testng.Assert;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.Binder;
import com.github.nill14.utils.moduledi.IModule;
import com.github.nill14.utils.moduledi.service.DeliveryService;
import com.github.nill14.utils.moduledi.service.IDeliveryService;
import com.github.nill14.utils.moduledi.service.ISnackService;
import com.google.inject.AbstractModule;

public class DeliveryModule extends AbstractModule implements IModule {

	@Override
	protected void configure() {
		bind(IDeliveryService.class).to(DeliveryService.class);
	}
	
	@Override
	public void buildServices(Binder binder) {
		binder.bind(IDeliveryService.class).to(DeliveryService.class);
		
	}
	

	@Override
	public void startModule(IBeanInjector beanInjector) {
		IDeliveryService service = beanInjector.getInstance(IDeliveryService.class);
		Assert.assertNotNull(service);
//		Assert.assertNotNull(service.getSnackService());
	}

	@Override
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
		// TODO Auto-generated method stub
		builder.usesOptionally(ISnackService.class);
	}
	


}

package com.github.nill14.utils.moduledi.module;

import org.junit.Assert;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.IModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
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
	public void buildServices(IServiceBuilder builder) {
		builder.addBean(DeliveryService.class, IDeliveryService.class);
		
	}

	@Override
	public void startModule(IServiceRegistry registry) {
		IDeliveryService service = registry.getService(IDeliveryService.class);
		Assert.assertNotNull(service);
//		Assert.assertNotNull(service.getSnackService());
	}

	@Override
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
		// TODO Auto-generated method stub
		builder.usesOptionally(ISnackService.class);
	}
	


}

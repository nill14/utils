package com.github.nill14.utils.moduledi.module;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.AbstractModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.bean.customer.TaskBean;
import com.github.nill14.utils.moduledi.service.DeliveryService;
import com.github.nill14.utils.moduledi.service.IDeliveryService;

public class DeliveryModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IDeliveryService.class).to(DeliveryService.class);
	}
	
	@Override
	public void buildServices(IServiceBuilder builder) {
		builder.addBean(DeliveryService.class, IDeliveryService.class);
		
	}
	


}

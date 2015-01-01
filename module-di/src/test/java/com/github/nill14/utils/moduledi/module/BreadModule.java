package com.github.nill14.utils.moduledi.module;

import com.github.nill14.utils.moduledi.AbstractModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.service.BreadService;
import com.github.nill14.utils.moduledi.service.IBreadService;

public class BreadModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IBreadService.class).to(BreadService.class);
	}

	@Override
	public void buildServices(IServiceBuilder builder) {
		builder.addBean(BreadService.class, IBreadService.class);
		
	}
	
	
}

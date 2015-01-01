package com.github.nill14.utils.moduledi.module;

import com.github.nill14.utils.moduledi.AbstractModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.service.ISnackService;
import com.github.nill14.utils.moduledi.service.SnackService;

public class SnackModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ISnackService.class).to(SnackService.class);
	}

	@Override
	public void buildServices(IServiceBuilder builder) {
		builder.addBean(SnackService.class, ISnackService.class);
	}
	
}

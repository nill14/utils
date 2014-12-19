package com.github.nill14.utils.moduledi;

public class SnackModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ISnackService.class).to(SnackService.class);
	}

}

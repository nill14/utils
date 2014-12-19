package com.github.nill14.utils.moduledi;

public class BreadModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IBreadService.class).to(BreadService.class);
	}

}

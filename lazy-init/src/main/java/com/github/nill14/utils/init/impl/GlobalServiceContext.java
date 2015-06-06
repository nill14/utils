package com.github.nill14.utils.init.impl;

import java.util.Optional;

import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IServiceContext;

public class GlobalServiceContext implements IServiceContext {

	public static final GlobalServiceContext instance() {
		return instance;
	}
	private static final GlobalServiceContext instance = new GlobalServiceContext();
	
	private GlobalServiceContext() {
	}

	@Override
	public Optional<IPojoInitializer> getInitializer() {
		return Optional.empty();
	}

	@Override
	public Optional<AbstractPropertyResolver> getCustomResolver() {
		return Optional.empty();
	}

}

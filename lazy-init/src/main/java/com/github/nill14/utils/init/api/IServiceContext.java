package com.github.nill14.utils.init.api;

import java.util.Optional;

import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
import com.github.nill14.utils.init.impl.GlobalServiceContext;

public interface IServiceContext {

	Optional<IPojoInitializer> getInitializer();
	
	Optional<AbstractPropertyResolver> getCustomResolver();
	
	
	public static IServiceContext global() {
		return GlobalServiceContext.instance();
	}
}

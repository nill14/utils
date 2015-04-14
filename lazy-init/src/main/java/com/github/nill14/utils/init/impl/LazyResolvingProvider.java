package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class LazyResolvingProvider<T> implements Provider<T>{

	
	private final IParameterType<T> type;
	private final IPropertyResolver resolver;

	public LazyResolvingProvider(IPropertyResolver resolver, IParameterType<T> type) {
		this.resolver = resolver;
		this.type = type;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		return (T) resolver.resolve(type);
	}

}

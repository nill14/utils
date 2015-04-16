package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class LazyResolvingProvider<T> implements Provider<T>{

	
	private final IParameterType type;
	private final IPropertyResolver resolver;

	public LazyResolvingProvider(IPropertyResolver resolver, IParameterType type) {
		this.resolver = resolver;
		this.type = type;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		T bean = (T) resolver.resolve(type);
		if (bean == null) {
			throw new RuntimeException(String.format(
					"Injection of bean %s failed!", type));
		}
		return bean;
	}

}

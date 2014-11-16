package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IObjectInitializer;

public class EmptyObjectInitializer<T> implements IObjectInitializer<T> {

	private static final EmptyObjectInitializer<?> instance = new EmptyObjectInitializer<>();
	@SuppressWarnings("unchecked")
	public static final <T> IObjectInitializer<T> getInstance() {
		return (IObjectInitializer<T>) instance;
	}
	
	private EmptyObjectInitializer() {
	}
	
	@Override
	public void init(T instance) { }

	@Override
	public void destroy(T instance) { }

}

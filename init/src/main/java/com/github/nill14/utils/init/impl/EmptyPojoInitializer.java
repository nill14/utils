package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IPojoInitializer;

public class EmptyPojoInitializer<T> implements IPojoInitializer<T> {

	private static final EmptyPojoInitializer<?> instance = new EmptyPojoInitializer<>();
	@SuppressWarnings("unchecked")
	public static final <T> IPojoInitializer<T> getInstance() {
		return (IPojoInitializer<T>) instance;
	}
	
	private EmptyPojoInitializer() {
	}
	
	@Override
	public void init(T instance) { }

	@Override
	public void destroy(T instance) { }

}

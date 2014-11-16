package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IObjectFactory;

public final class BeanObjectFactory<T> implements IObjectFactory<T> {
	
	public static <T> IObjectFactory<T> create(Class<T> beanClass) {
		return new BeanObjectFactory<>(beanClass);
	}

	private final Class<T> beanClass;
	
	private BeanObjectFactory(Class<T> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public T newInstance() {
		try {
			return beanClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<T> getType() {
		return beanClass;
	}

}

package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IPojoFactory;

public final class PojoFactory<T> implements IPojoFactory<T> {
	
	private static final long serialVersionUID = -8524486418807436934L;

	public static <T> IPojoFactory<T> create(Class<T> beanClass) {
		return new PojoFactory<>(beanClass);
	}

	private final Class<T> beanClass;
	
	private PojoFactory(Class<T> beanClass) {
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

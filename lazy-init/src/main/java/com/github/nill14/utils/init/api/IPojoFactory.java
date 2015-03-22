package com.github.nill14.utils.init.api;

import java.io.Serializable;

import javax.inject.Provider;

/**
 * 
 *
 * @deprecated Use {@link Provider} directly
 */
@Deprecated
public interface IPojoFactory<T> extends Serializable, Provider<T> {
	
	T newInstance();
	
	Class<T> getType();
	
	@Override
	default T get() {
		return newInstance();
	}
}

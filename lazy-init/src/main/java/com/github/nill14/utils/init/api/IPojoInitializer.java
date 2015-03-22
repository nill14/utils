package com.github.nill14.utils.init.api;

import java.io.Serializable;

import javax.inject.Provider;

import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.EmptyPojoInitializer;

public interface IPojoInitializer<T> extends Serializable {

	void init(Provider<?> factory, T instance);
	
	void destroy(Provider<?> factory, T instance);
	
	static <T> IPojoInitializer<T> empty() {
		return EmptyPojoInitializer.getInstance();
	}
	
	static IPojoInitializer<Object> standard() {
		return ChainingPojoInitializer.defaultInitializer();
	}
}

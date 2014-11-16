package com.github.nill14.utils.init.api;

import com.github.nill14.utils.init.impl.EmptyObjectInitializer;

public interface IObjectInitializer<T> {

	void init(T instance);
	
	default void destroy(T instance) { }
	
	static <T> IObjectInitializer<T> empty() {
		return EmptyObjectInitializer.getInstance();
	}
}

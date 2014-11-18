package com.github.nill14.utils.init.api;

import com.github.nill14.utils.init.impl.EmptyPojoInitializer;

public interface IPojoInitializer<T> {

	void init(T instance);
	
	void destroy(T instance);
	
	static <T> IPojoInitializer<T> empty() {
		return EmptyPojoInitializer.getInstance();
	}
}

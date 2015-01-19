package com.github.nill14.utils.init.api;

import java.io.Serializable;

import com.github.nill14.utils.init.impl.EmptyPojoInitializer;

public interface IPojoInitializer<T> extends Serializable {

	void init(ILazyPojo<?> lazyPojo, T instance);
	
	void destroy(ILazyPojo<?> lazyPojo, T instance);
	
	static <T> IPojoInitializer<T> empty() {
		return EmptyPojoInitializer.getInstance();
	}
}

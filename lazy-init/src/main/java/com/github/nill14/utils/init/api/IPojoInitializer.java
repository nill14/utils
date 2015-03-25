package com.github.nill14.utils.init.api;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.EmptyPojoInitializer;

public interface IPojoInitializer<T> extends Serializable {

	void init(@Nullable ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, T instance);
	
	void destroy(@Nullable ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, T instance);
	
	static <T> IPojoInitializer<T> empty() {
		return EmptyPojoInitializer.getInstance();
	}
	
	static IPojoInitializer<Object> standard() {
		return ChainingPojoInitializer.defaultInitializer();
	}
}

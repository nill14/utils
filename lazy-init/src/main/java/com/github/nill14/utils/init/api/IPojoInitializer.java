package com.github.nill14.utils.init.api;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.EmptyPojoInitializer;

public interface IPojoInitializer extends Serializable {

	void init(@Nullable ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance);
	
	void destroy(@Nullable ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance);
	
	static <T> IPojoInitializer empty() {
		return EmptyPojoInitializer.getInstance();
	}
	
	static IPojoInitializer standard() {
		return ChainingPojoInitializer.defaultInitializer();
	}
}

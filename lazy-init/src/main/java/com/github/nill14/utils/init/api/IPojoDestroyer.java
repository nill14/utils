package com.github.nill14.utils.init.api;

import java.io.Serializable;

public interface IPojoDestroyer extends Serializable {

	void destroy(IPojoFactory<?> pojoFactory, Object instance);
	
}

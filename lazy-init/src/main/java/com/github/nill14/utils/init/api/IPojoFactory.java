package com.github.nill14.utils.init.api;

import java.io.Serializable;

public interface IPojoFactory<T> extends Serializable {
	
	T newInstance();
	
	Class<T> getType();

}

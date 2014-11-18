package com.github.nill14.utils.init.api;

public interface IPojoFactory<T> {
	
	T newInstance();
	
	Class<T> getType();

}

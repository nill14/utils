package com.github.nill14.utils.init.api;

public interface IObjectFactory<T> {
	
	T newInstance();
	
	Class<T> getType();

}

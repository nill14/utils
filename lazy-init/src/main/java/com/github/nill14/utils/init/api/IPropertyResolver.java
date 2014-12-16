package com.github.nill14.utils.init.api;

public interface IPropertyResolver {

	Object resolve(Object pojo, Class<?> propertyType, String propertyName);
	
}

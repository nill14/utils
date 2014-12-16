package com.github.nill14.utils.init.api;

import java.io.Serializable;

public interface IPropertyResolver extends Serializable {

	Object resolve(Object pojo, Class<?> propertyType, String propertyName);
	
}

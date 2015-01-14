package com.github.nill14.utils.init.api;

import java.io.Serializable;

import javax.inject.Named;

public interface IPropertyResolver extends Serializable {

	/**
	 * 
	 * @param pojo The instance holding the property
	 * @param propertyType The type of the property and of the result.
	 * @param propertyName The {@link Named} value or the property name
	 * @return The resolved property or null if property could not be resolved.
	 */
	Object resolve(Object pojo, IType type);
	
}

package com.github.nill14.utils.init.api;

import java.io.Serializable;

public interface IPropertyResolver extends Serializable {

	/**
	 * 
	 * @param pojo The instance holding the property
	 * @param type The property type descriptor.
	 * @return The resolved property or null if property could not be resolved.
	 */
	Object resolve(Object pojo, IType type);
	
}

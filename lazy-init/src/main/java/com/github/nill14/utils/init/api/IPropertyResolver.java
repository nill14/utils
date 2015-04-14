package com.github.nill14.utils.init.api;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.impl.EmptyPropertyResolver;

public interface IPropertyResolver extends Serializable {

	/**
	 * 
	 * @param type The property type descriptor.
	 * @return The resolved property provider or nullProvider if property could not be resolved.
	 */
	@Nullable Object resolve(IParameterType<?> type);
	
	
	static IPropertyResolver empty() {
		return EmptyPropertyResolver.empty();
	}
	
//	static <T> Provider<T> nullProvider() {
//		return AbstractPropertyResolver.nullProvider();
//	}
}

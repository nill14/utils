package com.github.nill14.utils.init.api;

import java.io.Serializable;

public interface IPojoDestroyer extends Serializable {

	void destroy(IPropertyResolver resolver, IPojoFactory<?> pojoFactory, Object instance);

	static IPojoDestroyer empty() {
		return new IPojoDestroyer() {
			
			@Override
			public void destroy(IPropertyResolver resolver, IPojoFactory<?> pojoFactory, Object instance) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	static IPojoDestroyer standard() {
		return empty();
	}
}

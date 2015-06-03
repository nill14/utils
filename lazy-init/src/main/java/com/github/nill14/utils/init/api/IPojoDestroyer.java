package com.github.nill14.utils.init.api;

import java.io.Serializable;

public interface IPojoDestroyer extends Serializable {

	<T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance);

	static IPojoDestroyer empty() {
		return new IPojoDestroyer() {
			
			@Override
			public <T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	static IPojoDestroyer standard() {
		return empty();
	}
}

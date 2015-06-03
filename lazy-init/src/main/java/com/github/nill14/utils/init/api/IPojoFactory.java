package com.github.nill14.utils.init.api;

import java.io.Serializable;

import com.google.common.reflect.TypeToken;

public interface IPojoFactory<T> extends Serializable {
	
	T newInstance(IPropertyResolver resolver);
	
//	void destroyInstance(T instance);
	
	TypeToken<T> getType();
	
//	IPropertyResolver getResolver();
	
//	IPojoInitializer getInitializer();
	
	IBeanDescriptor<T> getDescriptor();
}

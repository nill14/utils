package com.github.nill14.utils.init.api;

import java.io.Serializable;

import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.EmptyPojoInitializer;

public interface IPojoInitializer extends IPojoDestroyer, Serializable {

	<T> void init(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance);
	
	
	static <T> IPojoInitializer empty() {
		return EmptyPojoInitializer.getInstance();
	}
	
	static IPojoInitializer standard() {
		return ChainingPojoInitializer.defaultInitializer();
	}
}

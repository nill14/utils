package com.github.nill14.utils.init.api;

import java.io.Serializable;

import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.EmptyPojoInitializer;

public interface IPojoInitializer extends IPojoDestroyer, Serializable {

	void init(IPropertyResolver resolver, IPojoFactory<?> pojoFactory, Object instance);
	
	
	static <T> IPojoInitializer empty() {
		return EmptyPojoInitializer.getInstance();
	}
	
	static IPojoInitializer standard() {
		return ChainingPojoInitializer.defaultInitializer();
	}
}

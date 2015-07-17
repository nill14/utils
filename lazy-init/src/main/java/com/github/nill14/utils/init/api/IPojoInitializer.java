package com.github.nill14.utils.init.api;

import java.io.Serializable;

import com.github.nill14.utils.init.impl.CallerContext;
import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.EmptyPojoInitializer;

public interface IPojoInitializer extends Serializable {

	<T> void init(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance, CallerContext context);
	
	default <T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {}
	
	
	static <T> IPojoInitializer empty() {
		return EmptyPojoInitializer.getInstance();
	}
	
	static IPojoInitializer standard() {
		return ChainingPojoInitializer.defaultInitializer();
	}
}

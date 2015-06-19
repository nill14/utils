package com.github.nill14.utils.init.api;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.impl.EmptyPropertyResolver;

public interface IPropertyResolver extends Serializable {

	/**
	 * 
	 * @param type The property type descriptor.
	 * @return The resolved property provider or nullProvider if property could not be resolved.
	 */
	@Nullable Object resolve(IParameterType type, ICallerContext context);
	
	
	static IPropertyResolver empty() {
		return EmptyPropertyResolver.empty();
	}
	
//	static <T> Provider<T> nullProvider() {
//		return AbstractPropertyResolver.nullProvider();
//	}
	
	IBeanInjector toBeanInjector(ICallerContext context);
	
	
	<T> void initializeBean(IBeanDescriptor<T> beanDescriptor, Object instance, ICallerContext context);
	
	<T> void destroyBean(IBeanDescriptor<T> beanDescriptor, Object instance);
	
	void insertInitializer(IPojoInitializer initializer);

	void appendInitializer(IPojoInitializer extraInitializer);

	List<IPojoInitializer> getInitializers(); 
	
}

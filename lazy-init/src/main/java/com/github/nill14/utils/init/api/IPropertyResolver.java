package com.github.nill14.utils.init.api;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.impl.CallerContext;
import com.github.nill14.utils.init.impl.EmptyPropertyResolver;

public interface IPropertyResolver extends Serializable {

	/**
	 * 
	 * @param type The property type descriptor.
	 * @param context 
	 * @return The resolved property provider or nullProvider if property could not be resolved.
	 */
	@Nullable Object resolve(IParameterType type, CallerContext context);
	
	
	static IPropertyResolver empty() {
		return EmptyPropertyResolver.empty();
	}
	
//	static <T> Provider<T> nullProvider() {
//		return AbstractPropertyResolver.nullProvider();
//	}
	
	IBeanInjector toBeanInjector(CallerContext context);
	
	
	<T> void initializeBean(IBeanDescriptor<T> beanDescriptor, Object instance, CallerContext context);
	
	<T> void destroyBean(IBeanDescriptor<T> beanDescriptor, Object instance);
	
	void insertInitializer(IPojoInitializer initializer);

	void appendInitializer(IPojoInitializer extraInitializer);

	List<IPojoInitializer> getInitializers(); 
	
}

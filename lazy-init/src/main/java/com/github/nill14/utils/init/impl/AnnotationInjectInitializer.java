package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.FieldInjectionDescriptor;
import com.github.nill14.utils.init.inject.ParameterTypeInjectionDescriptor;

@SuppressWarnings("serial")
public class AnnotationInjectInitializer implements IPojoInitializer<Object> {

	@Override
	public void init(Provider<?> factory, Object instance) {
		if (factory instanceof IBeanDescriptor && factory instanceof IPropertyResolver) {
			IBeanDescriptor<?> typeDescriptor = (IBeanDescriptor<?>) factory;
			IPropertyResolver resolver = (IPropertyResolver) factory;
			doInject(typeDescriptor, resolver, instance);
		}
	}

	@Override
	public void destroy(Provider<?> factory, Object instance) {
		
	}
	
	private void doInject(IBeanDescriptor<?> typeDescriptor, IPropertyResolver resolver, Object instance) {
		for (IMemberDescriptor fd : typeDescriptor.getFieldDescriptors()) {
			ParameterTypeInjectionDescriptor parameterType = ((FieldInjectionDescriptor) fd).getParameterType();
			injectParam(resolver, instance, fd, parameterType);
		}
	}

	private void injectParam(IPropertyResolver resolver, Object instance, IMemberDescriptor member, ParameterTypeInjectionDescriptor parameterType) {
		Object value = resolver.resolve(instance, parameterType);
		if (value instanceof LazyPojo) {
			throw new RuntimeException("Probably error in ServiceRegistry");
		}
		
		if (value != null) {
			try {
				member.invoke(instance, value);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(String.format(
						"Cannot inject property %s", member), e);
			}
			
		} 
		else if (!parameterType.isNullable()){ 
			throw new RuntimeException(String.format(
					"Cannot resolve property %s", member));
			
		}
	}
}

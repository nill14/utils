package com.github.nill14.utils.init.impl;

import java.util.Collection;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.FieldInjectionDescriptor;
import com.github.nill14.utils.init.inject.ParameterTypeInjectionDescriptor;

@SuppressWarnings("serial")
public final class AnnotationInjectInitializer implements IPojoInitializer {

	@Override
	public <T> void init(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
		//according to specification, fields are injected before methods
		for (IMemberDescriptor fd : beanDescriptor.getFieldDescriptors()) {
			ParameterTypeInjectionDescriptor parameterType = ((FieldInjectionDescriptor) fd).getParameterType();
			injectParam(resolver, instance, fd, parameterType);
		}

		for (IMemberDescriptor md : beanDescriptor.getMethodDescriptors()) {
			injectMethod(resolver, instance, md);
		}
	}

	@Override
	public <T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
		
	}

	private void injectParam(IPropertyResolver resolver, Object instance, IMemberDescriptor member, IParameterType parameterType) {
		Object value = resolver.resolve(parameterType);
		
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
	
	private void injectMethod(IPropertyResolver resolver, Object instance, IMemberDescriptor member) {
		
		try {
			Object[] args = createArgs(resolver, member.getParameterTypes());
			member.invoke(instance, args);
		} catch (ReflectiveOperationException | RuntimeException e) {
			throw new RuntimeException(String.format(
					"Cannot inject %s", member), e);
		}
			
	}	
	
	private Object[] createArgs(IPropertyResolver resolver, Collection<IParameterType> types) {
		if (types.isEmpty()) {
			return null;
		}
		
		Object[] args = new Object[types.size()];
		int i = 0;
		for (IParameterType type : types) {
			Object arg = resolver.resolve(type);
			if (null == arg && !type.isNullable()) {
				throw new RuntimeException(String.format("Cannot resolve property %s", type.getToken()));
			}
			args[i++] = arg;
		}
		return args;
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AnnotationInjectInitializer;
	}
}

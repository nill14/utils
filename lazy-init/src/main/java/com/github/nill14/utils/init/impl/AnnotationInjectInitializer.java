package com.github.nill14.utils.init.impl;

import java.util.Collection;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.FieldInjectionDescriptor;
import com.github.nill14.utils.init.inject.ParameterTypeInjectionDescriptor;

@SuppressWarnings("serial")
public class AnnotationInjectInitializer implements IPojoInitializer {

	@Override
	public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
		IBeanDescriptor<?> typeDescriptor = pojoFactory.getDescriptor();
		IPropertyResolver resolver = pojoFactory.getResolver();
		doInject(typeDescriptor, resolver, instance);
	}

	@Override
	public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
		
	}
	
	private void doInject(IBeanDescriptor<?> typeDescriptor, IPropertyResolver resolver, Object instance) {
		for (IMemberDescriptor md : typeDescriptor.getMethodDescriptors()) {
			injectMethod(resolver, instance, md);
		}
		
		for (IMemberDescriptor fd : typeDescriptor.getFieldDescriptors()) {
			ParameterTypeInjectionDescriptor<?> parameterType = ((FieldInjectionDescriptor) fd).getParameterType();
			injectParam(resolver, instance, fd, parameterType);
		}
	}

	private void injectParam(IPropertyResolver resolver, Object instance, IMemberDescriptor member, ParameterTypeInjectionDescriptor<?> parameterType) {
		Object value = resolver.resolve(instance, parameterType).get();
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
	
	private void injectMethod(IPropertyResolver resolver, Object instance, IMemberDescriptor member) {
		Object[] args = createArgs(resolver, member.getParameterTypes());
		
		try {
			member.invoke(instance, args);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(String.format(
					"Cannot inject %s", member), e);
		}
			
	}	
	
	private Object[] createArgs(IPropertyResolver resolver, Collection<IParameterType<?>> types) {
		if (types.isEmpty()) {
			return null;
		}
		
		Object[] args = new Object[types.size()];
		int i = 0;
		for (IParameterType<?> type : types) {
			Provider<?> arg = resolver.resolve(null, type);
			if (IPropertyResolver.nullProvider() == arg && !type.isNullable()) {
				throw new RuntimeException(String.format("Cannot resolve property %s", type.getToken()));
			}
			args[i++] = arg.get();
		}
		return args;
	}
}

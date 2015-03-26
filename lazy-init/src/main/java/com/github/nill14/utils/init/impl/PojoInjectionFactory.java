package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

public class PojoInjectionFactory<T> implements IPojoFactory<T> {
	
	private final IBeanDescriptor<T> beanDescriptor;
	private final IPropertyResolver resolver;
	
	public PojoInjectionFactory(TypeToken<T> typeToken, IPropertyResolver resolver) {
		this.beanDescriptor = new PojoInjectionDescriptor<>(typeToken);
		this.resolver = resolver;
		Preconditions.checkArgument(beanDescriptor.getConstructorDescriptors().size() > 0);
	}
	
	public PojoInjectionFactory(IBeanDescriptor<T> beanDescriptor, IPropertyResolver resolver) {
		this.beanDescriptor = beanDescriptor;
		this.resolver = resolver;
		Preconditions.checkArgument(beanDescriptor.getConstructorDescriptors().size() > 0);
	}

	@Override
	public T newInstance() {
		IMemberDescriptor injectionDescriptor = beanDescriptor.getConstructorDescriptors().get(0);
		try {
			return (T) injectionDescriptor.invoke(null);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(injectionDescriptor.toString(), e);
		}
	}
	

	@Override
	public TypeToken<T> getType() {
		return beanDescriptor.getToken();
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s)@%s", 
				getClass().getSimpleName(), 
				beanDescriptor.toString(), 
				Integer.toHexString(System.identityHashCode(this)));
	}

	@Override
	public IPropertyResolver getResolver() {
		return resolver;
	}
	
	@Override
	public IBeanDescriptor<T> getDescriptor() {
		return beanDescriptor;
	}

	private static final long serialVersionUID = -8524486418807436934L;
}

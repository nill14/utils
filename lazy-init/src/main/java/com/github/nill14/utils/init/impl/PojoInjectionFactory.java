package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

public final class PojoInjectionFactory<T> implements IPojoFactory<T> {
	

	public static <T> IPojoFactory<T> create(Class<T> beanClass, IPropertyResolver resolver) {
		return create(TypeToken.of(beanClass), resolver);
	}
	
	public static <T> IPojoFactory<T> create(TypeToken<T> typeToken, IPropertyResolver resolver) {
		PojoInjectionDescriptor<T> descriptor = new PojoInjectionDescriptor<>(typeToken);
		return new PojoInjectionFactory<>(typeToken, descriptor, resolver);
	}
	
	public static <T> IPojoFactory<T> create(IBeanDescriptor<T> descriptor, IPropertyResolver resolver) {
		return new PojoInjectionFactory<>(descriptor.getToken(), descriptor, resolver);
	}
	
	private final IBeanDescriptor<T> beanDescriptor;
	private final IPropertyResolver resolver;
	private final TypeToken<T> typeToken;
	
	private PojoInjectionFactory(TypeToken<T> typeToken, IBeanDescriptor<T> beanDescriptor, IPropertyResolver resolver) {
		this.typeToken = typeToken;
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
		return typeToken;
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

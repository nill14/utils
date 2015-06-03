package com.github.nill14.utils.init.impl;

import java.util.Collection;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

public class BeanTypePojoFactory<T> implements IPojoFactory<T> {
	
	private final IBeanDescriptor<T> beanDescriptor;
	
	public BeanTypePojoFactory(TypeToken<T> typeToken) {
		this.beanDescriptor = new PojoInjectionDescriptor<>(typeToken);
		Preconditions.checkArgument(beanDescriptor.getConstructorDescriptors().size() == 1, 
				typeToken + " does not have any suitable constructors! (expected exactly one)");
	}
	
	public BeanTypePojoFactory(IBeanDescriptor<T> beanDescriptor) {
		this.beanDescriptor = beanDescriptor;
		Preconditions.checkArgument(beanDescriptor.getConstructorDescriptors().size() > 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T newInstance(IPropertyResolver resolver) {
		T instance = doCreateInstance(resolver);
		resolver.initializeBean(getDescriptor(), instance);
		return instance;
	}

	private T doCreateInstance(IPropertyResolver resolver) {
		IMemberDescriptor injectionDescriptor = beanDescriptor.getConstructorDescriptors().get(0);
		
		try {
			Object[] args = createArgs(resolver, injectionDescriptor.getParameterTypes());
			return (T) injectionDescriptor.invoke(null, args);
		} catch (ReflectiveOperationException | RuntimeException e) {
			throw new RuntimeException(String.format(
					"Cannot inject constructor %s: %s", injectionDescriptor, e.getMessage()), e);
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
			if (arg == null && !type.isNullable()) {
				throw new RuntimeException(String.format("Cannot resolve property %s", type.getToken()));
			}
			args[i++] = arg;
		}
		return args;
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
	public IBeanDescriptor<T> getDescriptor() {
		return beanDescriptor;
	}

	private static final long serialVersionUID = -8524486418807436934L;
}

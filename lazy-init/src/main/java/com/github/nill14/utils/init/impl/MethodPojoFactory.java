package com.github.nill14.utils.init.impl;

import java.lang.reflect.Method;
import java.util.Collection;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.MethodInjectionDescriptor;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.reflect.TypeToken;

public final class MethodPojoFactory<T> implements IPojoFactory<T> {
	
	public static <T> MethodPojoFactory<T> of(TypeToken<T> typeToken, Method method, Object instance) {
		return new MethodPojoFactory<T>(typeToken, method, instance);
	}

	private static final long serialVersionUID = 1L;	
	private final TypeToken<T> typeToken;
	
    /** Cache the beanDescriptor */
    private IBeanDescriptor<T> beanDescriptor;

    private final Method method;
	private final Object instance; 
	
	public MethodPojoFactory(TypeToken<T> typeToken, Method m, Object instance) {
		this.typeToken = typeToken;
		this.method = m;
		this.instance = instance;
	}
	
	
	@Override
	public T newInstance(IPropertyResolver resolver) {
		T instance = injectMethod(resolver);
		if (instance != null) {
			resolver.initializeBean(getDescriptor(), instance);
		}
		return instance;
	}

	@Override
	public TypeToken<T> getType() {
		return typeToken;
	}

	
	@Override
	public IBeanDescriptor<T> getDescriptor() {
		//avoiding synchronization on purpose
		IBeanDescriptor<T> h = beanDescriptor;
		if (h == null) {
			h = new PojoInjectionDescriptor<>(typeToken);
			beanDescriptor = h;
		}
		return h;
	}
	
	
	@SuppressWarnings("unchecked")
	public T injectMethod(IPropertyResolver resolver) {
		MethodInjectionDescriptor member = new MethodInjectionDescriptor(method, method.getDeclaringClass());
		try {
			Object[] args = createArgs(resolver, member.getParameterTypes());
			return (T) member.invoke(instance, args);
		} catch (ReflectiveOperationException e) {
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
	
}

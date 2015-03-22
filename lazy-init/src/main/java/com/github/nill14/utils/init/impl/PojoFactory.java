package com.github.nill14.utils.init.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

public final class PojoFactory<T> implements Provider<T>, Serializable, IPropertyResolver, IBeanDescriptor<T> {
	
	private static final long serialVersionUID = -8524486418807436934L;

	public static <T> Provider<T> create(Class<T> beanClass) {
		return create(TypeToken.of(beanClass));
	}
	
	public static <T> Provider<T> create(TypeToken<T> typeToken) {
		IBeanDescriptor<T> pd = new PojoInjectionDescriptor<>(typeToken);
		return new PojoFactory<>(pd, EmptyPropertyResolver.empty());
	}

	private final IBeanDescriptor<T> beanDescriptor;
	private final IPropertyResolver resolver;
	
	/*package*/ PojoFactory(IBeanDescriptor<T> beanType, IPropertyResolver resolver) {
		this.beanDescriptor = beanType;
		this.resolver = resolver;
		Preconditions.checkArgument(beanType.getConstructorDescriptors().size() > 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		IMemberDescriptor injectionDescriptor = beanDescriptor.getConstructorDescriptors().get(0);
		try {
			return (T) injectionDescriptor.invoke(null);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(injectionDescriptor.toString(), e);
		}
	}

//	public Class<T> getType() {
//		try {
			//FIXME check this
			//javax.inject.Provider#get()
//			return (Class<T>) beanClass.getMethod("get").getReturnType();
//		} catch (NoSuchMethodException | SecurityException e) {
//			throw new RuntimeException(e);
//		}
		
//	}
	
	@Override
	public String toString() {
		return String.format("%s(%s)@%s", 
				getClass().getSimpleName(), 
				beanDescriptor.toString(), 
				Integer.toHexString(System.identityHashCode(this)));
	}

	@Override
	public Object resolve(Object pojo, IParameterType type) {
		return resolver.resolve(pojo, type);
	}

	@Override
	public List<? extends IMemberDescriptor> getFieldDescriptors() {
		return beanDescriptor.getFieldDescriptors();
	}

	@Override
	public List<? extends IMemberDescriptor> getMethodDescriptors() {
		return beanDescriptor.getMethodDescriptors();
	}

	@Override
	public List<? extends IMemberDescriptor> getConstructorDescriptors() {
		return beanDescriptor.getConstructorDescriptors();
	}

	@Override
	public Set<Class<? super T>> getInterfaces() {
		return beanDescriptor.getInterfaces();
	}

	@Override
	public Set<Class<? super T>> getDeclaredTypes() {
		return beanDescriptor.getDeclaredTypes();
	}

	@Override
	public Set<Annotation> getDeclaredQualifiers() {
		return beanDescriptor.getDeclaredQualifiers();
	}

	@Override
	public Class<T> getRawType() {
		return beanDescriptor.getRawType();
	}

	@Override
	public Type getGenericType() {
		return beanDescriptor.getGenericType();
	}

	@Override
	public boolean canBeInstantiated() {
		return beanDescriptor.canBeInstantiated();
	}

}

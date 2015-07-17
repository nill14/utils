package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.reflect.TypeToken;

public final class BeanInstancePojoFactory<T> implements IPojoFactory<T> {

	@SuppressWarnings("unchecked")
	public static <T> IPojoFactory<T> singleton(T singleton) {
		return new BeanInstancePojoFactory<T>((TypeToken<T>) TypeToken.of(singleton.getClass()), singleton);
	}
	
	public static <T> BeanInstancePojoFactory<T> nullFactory(Class<T> nullType) {
		return new BeanInstancePojoFactory<T>(TypeToken.of(nullType), null);
	}
	
	public static <T> BeanInstancePojoFactory<T> of(TypeToken<T> typeToken, T instance) {
		return new BeanInstancePojoFactory<T>(typeToken, instance);
	}

	private static final long serialVersionUID = 1L;	
	private final TypeToken<T> typeToken;
	private final T instance;
	
    /** Cache the beanDescriptor */
    private IBeanDescriptor<T> beanDescriptor; 

	
	public BeanInstancePojoFactory(TypeToken<T> typeToken, T instance) {
		this.typeToken = typeToken;
		this.instance = instance;
	}
	
	protected BeanInstancePojoFactory(IBeanDescriptor<T> beanDescriptor, T instance) {
		this.typeToken = beanDescriptor.getToken();
		this.beanDescriptor = beanDescriptor;
		this.instance = instance;
	}
	
	@Override
	public T newInstance(IPropertyResolver resolver, CallerContext context) {
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
	
}

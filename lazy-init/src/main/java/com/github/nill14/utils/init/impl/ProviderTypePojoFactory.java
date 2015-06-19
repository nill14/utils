package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.ICallerContext;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.github.nill14.utils.init.inject.ReflectionUtils;
import com.google.common.reflect.TypeToken;

public final class ProviderTypePojoFactory<T, F extends Provider<? extends T>> implements IPojoFactory<T> {
	
	private static final long serialVersionUID = 1L;
	private final TypeToken<T> typeToken;
	private final IPojoFactory<F> pojoFactory;

    /** Cache the beanDescriptor */
    private IBeanDescriptor<T> beanDescriptor;

	public ProviderTypePojoFactory(TypeToken<F> providerType) {
		this.typeToken = ReflectionUtils.getProviderReturnTypeToken(providerType);
		this.pojoFactory = new BeanTypePojoFactory<>(providerType);
	}
    
	@SuppressWarnings("unused")
	private <P extends Provider<? extends T>> ProviderTypePojoFactory(IPojoFactory<F> pojoFactory, TypeToken<T> typeToken, IPropertyResolver resolver) {
		this.pojoFactory = pojoFactory;
		this.typeToken = typeToken;
	}

	@Override
	public T newInstance(IPropertyResolver resolver, ICallerContext context) {
		T instance = pojoFactory.newInstance(resolver, context).get();
		if (instance != null) {
			resolver.initializeBean(getDescriptor(), instance, context);
		}
		return instance;
	}

	@Override
	public TypeToken<T> getType() {
		return typeToken;
	}
	
	public TypeToken<F> getFactoryType() {
		return pojoFactory.getType();
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
	
	public IPojoFactory<F> getNestedPojoFactory() {
		return pojoFactory;
	}

}
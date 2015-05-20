package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.github.nill14.utils.init.inject.ReflectionUtils;
import com.google.common.reflect.TypeToken;

public class PojoFactoryAdapter<T, F extends Provider<? extends T>> implements IPojoFactory<T> {
	
	private static final long serialVersionUID = 1L;
	private final TypeToken<T> typeToken;
	private final IPojoFactory<F> pojoFactory;

    /** Cache the beanDescriptor */
    private IBeanDescriptor<T> beanDescriptor;
	private IPropertyResolver resolver;

	public PojoFactoryAdapter(TypeToken<F> providerType, IPropertyResolver resolver) {
		this.typeToken = ReflectionUtils.getProviderReturnTypeToken(providerType);
		this.pojoFactory = new PojoInjectionFactory<>(providerType, resolver);
	}
    
	@SuppressWarnings("unused")
	private <P extends Provider<? extends T>> PojoFactoryAdapter(IPojoFactory<F> pojoFactory, TypeToken<T> typeToken, IPropertyResolver resolver) {
		this.pojoFactory = pojoFactory;
		this.typeToken = typeToken;
		this.resolver = resolver;
	}

	@Override
	public T newInstance() {
		T instance = pojoFactory.newInstance().get();
		resolver.initializeBean(instance);
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
	public IPropertyResolver getResolver() {
		return pojoFactory.getResolver();
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
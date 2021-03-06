package com.github.nill14.utils.init.integration;


import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.impl.ProviderTypePojoFactory;
import com.github.nill14.utils.init.impl.BeanTypePojoFactory;
import com.github.nill14.utils.init.impl.CallerContext;
import com.google.common.reflect.TypeToken;

public /*non-final on purpose*/ class LazyPojoFactory<F> implements IPojoFactory<F> {


	public static <T> LazyPojoFactory<T> forClass(Class<T> beanClass) {
		return new LazyPojoFactory<>(TypeToken.of(beanClass));
	}

	public static <T, F extends Provider<T>> LazyPojoFactory<T> forFactory(Class<F> factoryType) {
		return new LazyPojoFactory<>(TypeToken.of(factoryType), factoryType);
	}

	
	private static final long serialVersionUID = 1L;
	
	
	private final boolean doubleFactory;
	private final IPojoFactory<F> delegate;
	private final TypeToken<F> factoryToken;


	protected LazyPojoFactory(TypeToken<F> beanType) {
		delegate = new BeanTypePojoFactory<>(beanType);
		factoryToken = beanType;
		this.doubleFactory = false;
	}

	//class type F is here G - factory
	@SuppressWarnings("unchecked")
	protected <T, G extends Provider<T>> LazyPojoFactory(TypeToken<G> factoryType, Class<G> factoryClass) {
		ProviderTypePojoFactory<T, G> factoryAdapter = new ProviderTypePojoFactory<T, G>(factoryType);
		this.factoryToken = (TypeToken<F>) factoryType;
		this.delegate = (IPojoFactory<F>) factoryAdapter;
		this.doubleFactory = true;
	}
	
	public boolean isDoubleFactory() {
		return doubleFactory;
	}

	@Override
	public F newInstance(IPropertyResolver resolver, CallerContext context) {
		F instance = delegate.newInstance(resolver, context);
		resolver.initializeBean(getDescriptor(), instance, context);
		return instance;
	}

	@Override
	public TypeToken<F> getType() {
		return factoryToken;
	}

	@Override
	public IBeanDescriptor<F> getDescriptor() {
		return delegate.getDescriptor();
	}
	
}

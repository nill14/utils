package com.github.nill14.utils.init.integration;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.impl.PojoFactoryAdapter;
import com.github.nill14.utils.init.impl.PojoInjectionFactory;
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
		delegate = new PojoInjectionFactory<>(beanType);
		factoryToken = beanType;
		this.doubleFactory = false;
	}

	//class type F is here G - factory
	@SuppressWarnings("unchecked")
	protected <T, G extends Provider<T>> LazyPojoFactory(TypeToken<G> factoryType, Class<G> factoryClass) {
		PojoFactoryAdapter<T, G> factoryAdapter = new PojoFactoryAdapter<T, G>(factoryType);
		this.factoryToken = (TypeToken<F>) factoryType;
		this.delegate = (IPojoFactory<F>) factoryAdapter;
		this.doubleFactory = true;
	}
	
	public boolean isDoubleFactory() {
		return doubleFactory;
	}

	@Override
	public F newInstance(IPropertyResolver resolver) {
		F instance = delegate.newInstance(resolver);
		resolver.initializeBean(instance);
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

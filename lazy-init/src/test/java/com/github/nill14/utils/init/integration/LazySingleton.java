package com.github.nill14.utils.init.integration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.impl.LazyPojo;
import com.github.nill14.utils.init.impl.PojoProviderFactory;
import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public class LazySingleton<T> implements ILazyPojo<T>, IExtraFactory<T> {

	public static <T> LazySingleton<T> newProxy(ILazyPojo<T> lazyPojo) {
		return new LazySingleton<T>(lazyPojo);
	}

	public static <T> LazySingleton<T> of(Class<T> type, Supplier<T> factory) {
		ILazyPojo<T> lazyPojo = LazyPojo.forProvider(() -> factory.get(), IPropertyResolver.empty(),
				IPojoInitializer.empty());
		return newProxy(lazyPojo);
	}

	@SuppressWarnings("unchecked")
	public static <T> ILazyPojo<T> lazyNull() {
		PojoProviderFactory<Object> nullFactory = PojoProviderFactory.nullFactory(Object.class);
		return (ILazyPojo<T>) LazyPojo.forFactory(nullFactory, IPojoInitializer.empty());
	}

	private final ILazyPojo<T> delegate;

	private LazySingleton(ILazyPojo<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T createBean() {
		return getInstance();
	}

	@Override
	public T getInstance() {
		return delegate.getInstance();
	}

	@Override
	public boolean freeInstance() {
		return delegate.freeInstance();
	}

	@Override
	public TypeToken<T> getType() {
		return delegate.getType();
	}

	@Override
	public Future<T> init(ExecutorService executor) {
		return delegate.init(executor);
	}

	@Override
	public Future<Boolean> destroy(ExecutorService executor) {
		return delegate.destroy(executor);
	}
	
	@Override
	public Provider<T> toProvider() {
		return delegate.toProvider();
	}

}
package com.github.nill14.utils.init.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.ICallerContext;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public final class LazyPojo<T> implements ILazyPojo<T> {

	
	public static <T> ILazyPojo<T> forSingleton(T singleton) {
		return forSingleton(singleton, IPropertyResolver.empty());
	}
	
	public static <T> ILazyPojo<T> forSingleton(T singleton, IPropertyResolver resolver) {
		IPojoFactory<T> pojoFactory = BeanInstancePojoFactory.singleton(singleton);
		return new LazyPojo<T>(pojoFactory, resolver);
	}
	
	public static <T> ILazyPojo<T> forBean(Class<T> beanClass) {
		return forBean(beanClass, IPropertyResolver.empty());
	}
	
	public static <T> ILazyPojo<T> forBean(Class<T> beanClass, IPropertyResolver resolver) {
		IPojoFactory<T> factory = new BeanTypePojoFactory<>(TypeToken.of(beanClass));
		return new LazyPojo<>(factory, resolver);
	}
	
	public static <T, F extends Provider<? extends T>> ILazyPojo<T> forProvider(Class<F> providerClass) {
		return forProvider(providerClass, IPropertyResolver.empty());
	}
	
	public static <T, F extends Provider<? extends T>> ILazyPojo<T> forProvider(
			Class<F> providerClass, IPropertyResolver resolver) {
		
		TypeToken<F> providerType = TypeToken.of(providerClass);
		ProviderTypePojoFactory<T, F> factoryAdapter = new ProviderTypePojoFactory<T, F>(providerType);
		return new LazyPojo<>(factoryAdapter, resolver);
	}
	
	public static <T> ILazyPojo<T> forProvider(TypeToken<T> returnType,
			Provider<T> provider) {
		return forProvider(returnType, provider, IPropertyResolver.empty());
	}
	
	public static <T> ILazyPojo<T> forProvider(TypeToken<T> returnType,
			Provider<T> provider, IPropertyResolver resolver) {
		IPojoFactory<T> pojoFactory = new ProviderInstancePojoFactory<>(returnType, provider);
		return new LazyPojo<>(pojoFactory, resolver);
	}
	
	public static <T> ILazyPojo<T> forFactory(
			IPojoFactory<T> pojoFactory, IPropertyResolver resolver) {
		return new LazyPojo<>(pojoFactory, resolver);
	}
	
	private final IPojoFactory<T> factory;
	private final IPropertyResolver resolver;
	private volatile transient T instance;

	public LazyPojo(IPojoFactory<T> factory, IPropertyResolver resolver) {
		this.factory = factory;
		this.resolver = resolver;
	}
	
	@Override
	public TypeToken<T> getType() {
		return factory.getType();
	}

	@Override
	public T getInstance() {
		T instance = this.instance;
		if (instance == null) {
			
			synchronized (this) {
				instance = this.instance;
				if (instance == null) {
					
					instance = factory.newInstance(resolver, ICallerContext.prototype());
					this.instance = instance;
				}
			}
		}
		return instance;
	}

	@Override
	public boolean freeInstance() {
		boolean released = false;
		T instance;
		
		synchronized (this) {
			instance = this.instance;
			this.instance = null;
		}
		
		if (instance != null) {
			resolver.destroyBean(factory.getDescriptor(), instance);
			released = true;
		}
		
		return released;
	}

	@Override
	public Future<T> init(ExecutorService executor) {
		return executor.submit(new Callable<T>() {

			@Override
			public T call() throws Exception {
				return getInstance();
			}
		});
	}

	@Override
	public Future<Boolean> destroy(ExecutorService executor) {
		return executor.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return freeInstance();
			}
		});
	}
	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		synchronized (this) {
			if (instance instanceof Serializable) {
				stream.writeObject(instance);
			} else {
				stream.writeObject(null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		Object obj = stream.readObject();
		synchronized (this) {
			if (obj != null) {
				this.instance = (T) obj;
			}
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s)@%s", 
				getClass().getSimpleName(), 
				factory.getType().toString(), 
				Integer.toHexString(System.identityHashCode(this)));
	}
	
	@Override
	public Provider<T> toProvider() {
		return provider;
	}

	
	private transient Provider<T> provider = new Provider<T>() {

		@Override
		public T get() {
			return getInstance();
		}
	};

}

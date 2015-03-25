package com.github.nill14.utils.init.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public final class LazyPojo<T> implements ILazyPojo<T>, Provider<T> {

	
	public static <T> ILazyPojo<T> forSingleton(T singleton) {
		return forSingleton(singleton, IPropertyResolver.empty());
	}
	
	public static <T> ILazyPojo<T> forSingleton(T singleton, IPropertyResolver resolver) {
		IPojoFactory<T> pojoFactory = PojoProviderFactory.singleton(singleton, resolver);
		return new LazyPojo<T>(pojoFactory, IPojoInitializer.empty());
	}
	
	public static <T> ILazyPojo<T> forBean(Class<T> beanClass) {
		return forBean(beanClass, IPropertyResolver.empty(), IPojoInitializer.empty());
	}
	
	public static <T> ILazyPojo<T> forBean(Class<T> beanClass, IPropertyResolver resolver, IPojoInitializer<? super T> initializer) {
		IPojoFactory<T> factory = PojoInjectionFactory.create(beanClass, resolver);
		return new LazyPojo<>(factory, initializer);
	}
	
	public static <T, F extends Provider<? extends T>> ILazyPojo<T> forProvider(Class<F> providerClass) {
		return forProvider(providerClass, IPropertyResolver.empty(), IPojoInitializer.empty());
	}
	
	public static <T, F extends Provider<? extends T>> ILazyPojo<T> forProvider(
			Class<F> providerClass, IPropertyResolver resolver, IPojoInitializer<? super F> factoryInitializer) {
		TypeToken<T> typeToken = PojoProviderFactory.getProviderReturnTypeToken(providerClass);
		IPojoFactory<F> pojoFactory = PojoInjectionFactory.create(providerClass, resolver);
		FactoryAdapter<T, F> factoryAdapter = new FactoryAdapter<T, F>(pojoFactory, typeToken, factoryInitializer);
		return new LazyPojo<>(factoryAdapter, factoryAdapter);
	}
	
	public static <T> ILazyPojo<T> forProvider(
			Provider<T> provider, IPropertyResolver resolver, IPojoInitializer<? super T> initializer) {
		IPojoFactory<T> pojoFactory = PojoProviderFactory.create(provider, resolver);
		return new LazyPojo<>(pojoFactory, initializer);
	}
	
	public static <T> ILazyPojo<T> forFactory(
			IPojoFactory<T> pojoFactory, IPojoInitializer<? super T> initializer) {
		return new LazyPojo<>(pojoFactory, initializer);
	}
	
	private final IPojoFactory<T> factory;
	private final IPojoInitializer<? super T> initializer;
	private volatile transient T instance;

	public LazyPojo(IPojoFactory<T> factory, IPojoInitializer<? super T> initializer) {
		this.factory = factory;
		this.initializer = initializer;
	}
	
	@Override
	public TypeToken<T> getType() {
		return factory.getType();
	}

	@Override
	public T get() {
		return getInstance();
	}
	
	@Override
	public T getInstance() {
		T instance = this.instance;
		if (instance == null) {
			
			synchronized (this) {
				instance = this.instance;
				if (instance == null) {
					
					instance = factory.newInstance();
					initializer.init(this, factory, instance);
					this.instance = instance;
				}
			}
		}
		return instance;
	}

	@Override
	public boolean freeInstance() {
		boolean released = false;
		T instance = this.instance;
		if (instance != null) {
			
			synchronized (this) {
				instance = this.instance;
				if (instance != null) {
					
					this.instance = null;
					initializer.destroy(this, factory, instance);
					released = true;
				}
			}
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
	
	public static class FactoryAdapter<T, F extends Provider<? extends T>> implements IPojoFactory<T>, IPojoInitializer<T> {
		
		private final ILazyPojo<F> lazyFactory;
		private final TypeToken<T> typeToken;
		private final IPojoFactory<F> pojoFactory;

		public FactoryAdapter(IPojoFactory<F> pojoFactory, TypeToken<T> typeToken, IPojoInitializer<? super F> factoryInitializer) {
			this.pojoFactory = pojoFactory;
			this.typeToken = typeToken;
			this.lazyFactory = new LazyPojo<F>(pojoFactory, factoryInitializer);
		}

		@Override
		public T newInstance() {
			return lazyFactory.getInstance().get();
		}

		@Override
		public TypeToken<T> getType() {
			return typeToken;
		}
		
		@Override
		public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, T instance) {
			//nothing to do, we want to initialize pojoFactory, not the instance created by the factory
			//factoryInitializer was invoked by lazyFactory.getInstance() call
			
			//FIXME inject also pojo, not only pojo factory
		}
		
		@Override
		public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, T instance) {
			//delegate the destruction to the factoryInitializer
			//the factory is not re-used but re-created for each object
			lazyFactory.freeInstance();
		}
		
		@Override
		public IPropertyResolver getResolver() {
			return pojoFactory.getResolver();
		}
		
		@Override
		public IBeanDescriptor<T> getDescriptor() {
			// TODO Auto-generated method stub
			return null;
		}

	}

}

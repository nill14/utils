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
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;

@SuppressWarnings("serial")
public class LazyPojo<T> implements ILazyPojo<T>, Provider<T> {

	@SuppressWarnings("unchecked")
	public static <T> ILazyPojo<T> forSingleton(T singleton) {
		IBeanDescriptor<T> pd = new PojoInjectionDescriptor<>((Class<T>) singleton.getClass());
		return new LazyPojo<T>(() -> singleton, pd, IPojoInitializer.empty());
	}
	
	public static <T> ILazyPojo<T> forClass(Class<T> beanClass) {
		return forClass(beanClass, IPropertyResolver.empty(), IPojoInitializer.empty());
	}
	
	public static <T, F extends Provider<? extends T>> ILazyPojo<T> forFactory(Class<T> beanClass, Class<F> factoryClass) {
		return forFactory(beanClass, factoryClass, IPropertyResolver.empty(), IPojoInitializer.empty());
	}
	
	public static <T> ILazyPojo<T> forClass(Class<T> beanClass, IPropertyResolver resolver, IPojoInitializer<? super T> initializer) {
		IBeanDescriptor<T> pd = new PojoInjectionDescriptor<>(beanClass);
		Provider<T> factory = new PojoFactory<>(pd, resolver);
		return new LazyPojo<>(factory, pd, initializer);
	}

	public static <T, F extends Provider<? extends T>> ILazyPojo<T> forFactory(Class<T> beanClass,
			Class<F> factoryClass, IPropertyResolver resolver, IPojoInitializer<? super F> factoryInitializer) {
	IBeanDescriptor<T> pd = new PojoInjectionDescriptor<>(beanClass);
		IBeanDescriptor<F> pdFactory = new PojoInjectionDescriptor<>(factoryClass);
		FactoryAdapter<T, F> factoryAdapter = new FactoryAdapter<T, F>(beanClass, pdFactory, factoryInitializer);
		return new LazyPojo<>(factoryAdapter, pd, factoryAdapter);
	}
	
	private final Provider<T> factory;
	private final IBeanDescriptor<T> pojoType;
	private final IPojoInitializer<? super T> initializer;
	private volatile transient T instance;

	public LazyPojo(Provider<T> factory, IBeanDescriptor<T> pojoType, IPojoInitializer<? super T> initializer) {
		this.factory = factory;
		this.pojoType = pojoType;
		this.initializer = initializer;
	}
	
	@Override
	public Class<? extends T> getInstanceType() {
		return pojoType.getRawType();
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
					
					instance = factory.get();
					initializer.init(factory, instance);
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
					initializer.destroy(factory, instance);
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

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		Object obj = stream.readObject();
		synchronized (this) {
			if (obj != null) {
				this.instance = getInstanceType().cast(obj);
			}
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s)@%s", 
				getClass().getSimpleName(), 
				pojoType.toString(), 
				Integer.toHexString(System.identityHashCode(this)));
	}
	
	public static class FactoryAdapter<T, F extends Provider<? extends T>> implements Provider<T>, IPojoInitializer<T> {
		
		private final ILazyPojo<F> lazyFactory;

		public FactoryAdapter(Class<T> beanClass, IBeanDescriptor<F> factoryType, IPojoInitializer<? super F> factoryInitializer) {
			Provider<F> factoryFactory = new PojoFactory<>(factoryType, IPropertyResolver.empty());
			this.lazyFactory = new LazyPojo<>(factoryFactory, factoryType, factoryInitializer);
		}


		@Override
		public T get() {
			return lazyFactory.getInstance().get();
		}


		@Override
		public void init(Provider<?> factory, T instance) {
			//nothing to do, we want to initialize pojoFactory, not the instance created by the factory
			//factoryInitializer was invoked by lazyFactory.getInstance() call
		}
		
		@Override
		public void destroy(Provider<?> factory, T instance) {
			//delegate the destruction to the factoryInitializer
			//the factory is not re-used but re-created for each object
			lazyFactory.freeInstance();
		}

	}

}

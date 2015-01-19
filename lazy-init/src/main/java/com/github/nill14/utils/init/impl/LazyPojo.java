package com.github.nill14.utils.init.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;

@SuppressWarnings("serial")
public class LazyPojo<T> implements ILazyPojo<T>, Provider<T> {

	public static <T> ILazyPojo<T> forClass(Class<T> beanClass) {
		return forClass(beanClass, IPojoInitializer.empty());
	}
	
	public static <T, F extends IPojoFactory<T>> ILazyPojo<T> forFactory(Class<T> beanClass, Class<F> factoryClass) {
		return forFactory(beanClass, factoryClass, IPojoInitializer.empty());
	}
	
	public static <T> ILazyPojo<T> forClass(Class<T> beanClass, IPojoInitializer<? super T> initializer) {
		IPojoFactory<T> factory = PojoFactory.create(beanClass);
		return new LazyPojo<>(factory, initializer);
	}

	public static <T, F extends IPojoFactory<? extends T>> ILazyPojo<T> forFactory(Class<T> beanClass, Class<F> factoryClass, IPojoInitializer<? super F> factoryInitializer) {
		IPojoFactory<F> factoryFactory = PojoFactory.create(factoryClass);
		FactoryAdapter<T, F> factoryAdapter = new FactoryAdapter<>(beanClass, factoryFactory, factoryInitializer);
		return new LazyPojo<>(factoryAdapter, factoryAdapter);
	}
	
	private final IPojoFactory<? extends T> factory;
	private final IPojoInitializer<? super T> initializer;
	private volatile transient T instance;

	public LazyPojo(IPojoFactory<? extends T> factory, IPojoInitializer<? super T> initializer) {
		this.factory = factory;
		this.initializer = initializer;
	}
	
	@Override
	public Class<? extends T> getInstanceType() {
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
					initializer.init(this, instance);
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
					initializer.destroy(this, instance);
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
	
	
	public static class FactoryAdapter<T, F extends IPojoFactory<? extends T>> implements IPojoFactory<T>, IPojoInitializer<T> {
		
		private final ILazyPojo<F> lazyFactory;
		private final Class<T> beanClass;

		@SuppressWarnings("unchecked")
		public FactoryAdapter(Class<T> beanClass, IPojoFactory<F> factoryFactory, IPojoInitializer<? super F> factoryInitializer) {
			this.lazyFactory = new LazyPojo<>(factoryFactory, factoryInitializer);
			this.beanClass = beanClass;
		}


		@Override
		public T newInstance() {
			return lazyFactory.getInstance().newInstance();
		}

		@Override
		public Class<T> getType() {
			//type of the bean, not of the factory
			return beanClass;
		}


		@Override
		public void init(ILazyPojo<?> lazyPojo, T instance) {
			//nothing to do, we want to initialize pojoFactory, not the instance created by the factory
			//factoryInitializer was invoked by lazyFactory.getInstance() call
		}
		
		@Override
		public void destroy(ILazyPojo<?> lazyPojo, T instance) {
			//delegate the destruction to the factoryInitializer
			//the factory is not re-used but re-created for each object
			lazyFactory.freeInstance();
		}

	}

}

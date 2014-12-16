package com.github.nill14.utils.init.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;

public class LazyPojo<T> implements ILazyPojo<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5927142279116185259L;

	public static <T> ILazyPojo<T> forClass(Class<T> beanClass) {
		return forClass(beanClass, IPojoInitializer.empty());
	}

	public static <T> ILazyPojo<T> forFactory(Class<T> beanClass, Class<? extends IPojoFactory<T>> factoryClass) {
		return forFactory(beanClass, factoryClass, IPojoInitializer.empty());
	}
	
	public static <T> ILazyPojo<T> forClass(Class<T> beanClass, IPojoInitializer<? super T> initializer) {
		IPojoFactory<T> factory = PojoFactory.create(beanClass);
		return new LazyPojo<>(factory, initializer);
	}

	public static <T, F extends IPojoFactory<? extends T>> ILazyPojo<T> forFactory(Class<T> beanClass, Class<F> factoryClass, IPojoInitializer<? super F> factoryInitializer) {
		FactoryAdapter<T, F> factoryAdapter = new FactoryAdapter<>(beanClass, factoryClass, factoryInitializer);
		return new LazyPojo<>(factoryAdapter, factoryAdapter);
	}
	
	private final IPojoFactory<? extends T> factory;
	private final IPojoInitializer<? super T> initializer;
	private volatile T instance;

	public LazyPojo(IPojoFactory<? extends T> factory, IPojoInitializer<? super T> initializer) {
		this.factory = factory;
		this.initializer = initializer;
	}
	
	@Override
	public Class<? extends T> getInstanceType() {
		return factory.getType();
	}
	
	@Override
	public T getInstance() {
		T instance = this.instance;
		if (instance == null) {
			
			synchronized (this) {
				instance = this.instance;
				if (instance == null) {
					
					instance = factory.newInstance();
					initializer.init(instance);
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
					initializer.destroy(instance);
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
	
	
	private static class FactoryAdapter<T, F extends IPojoFactory<? extends T>> implements IPojoFactory<T>, IPojoInitializer<T> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3988519477717092909L;
		
		private final ILazyPojo<F> lazyFactory;
		private final Class<T> beanClass;

		public FactoryAdapter(Class<T> beanClass, Class<F> factoryClass, IPojoInitializer<? super F> factoryInitializer) {
			IPojoFactory<F> factoryFactory = PojoFactory.create(factoryClass);
			this.lazyFactory = new LazyPojo<>(factoryFactory, factoryInitializer);
			this.beanClass = beanClass;
		}


		@Override
		public T newInstance() {
			return lazyFactory.getInstance().newInstance();
		}

		@Override
		public Class<T> getType() {
			return beanClass;
		}


		@Override
		public void init(T instance) {
			//nothing to do, here is already late
		}
		
		@Override
		public void destroy(T instance) {
			//destroying of bean is being handled by the factory
			//the factory is not re-used but re-created for each object
			lazyFactory.freeInstance();
		}

	}

}

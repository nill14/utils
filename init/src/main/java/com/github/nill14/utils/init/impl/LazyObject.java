package com.github.nill14.utils.init.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.github.nill14.utils.init.api.ILazyObject;
import com.github.nill14.utils.init.api.IObjectFactory;
import com.github.nill14.utils.init.api.IObjectInitializer;

public class LazyObject<T> implements ILazyObject<T> {

	
	private final IObjectFactory<? extends T> factory;
	private final IObjectInitializer<? super T> initializer;
	private volatile T instance;

	public LazyObject(IObjectFactory<? extends T> factory, IObjectInitializer<? super T> initializer) {
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

}

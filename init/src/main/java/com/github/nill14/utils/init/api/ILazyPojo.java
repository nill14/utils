package com.github.nill14.utils.init.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface ILazyPojo<T> {

	T getInstance();
	
	boolean freeInstance();
	
	Class<? extends T> getInstanceType();
	
	Future<T> init(ExecutorService executor);
	
	Future<Boolean> destroy(ExecutorService executor);
}

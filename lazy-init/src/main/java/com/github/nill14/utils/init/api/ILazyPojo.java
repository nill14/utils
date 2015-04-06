package com.github.nill14.utils.init.api;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Provider;

import com.google.common.reflect.TypeToken;

public interface ILazyPojo<T> extends Serializable {

	T getInstance();
	
	boolean freeInstance();

	TypeToken<T> getType();
	
	Future<T> init(ExecutorService executor);
	
	Future<Boolean> destroy(ExecutorService executor);
	
	Provider<T> toProvider();

}

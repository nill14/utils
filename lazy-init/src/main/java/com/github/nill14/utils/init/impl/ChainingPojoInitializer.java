package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public class ChainingPojoInitializer<T> implements IPojoInitializer<T> {
	
	private final ImmutableList<IPojoInitializer<? super T>> items;
	
	
	@SafeVarargs
	public ChainingPojoInitializer(IPojoInitializer<? super T>... initializers) {
		items = ImmutableList.copyOf(initializers);
	}

	public ChainingPojoInitializer(ImmutableList<IPojoInitializer<? super T>> initializers) {
		items = initializers;
	}
	
	public static ChainingPojoInitializer<Object> defaultInitializer() {
		return new ChainingPojoInitializer<>(
				new AnnotationInjectInitializer(),
				new AnnotationLifecycleInitializer(),
				new EventBusPojoInitializer());
	}
	
	/**
	 * 
	 * @param extraInitializer The first initializer to execute
	 * @return
	 */
	public <S extends T> ChainingPojoInitializer<S> with(IPojoInitializer<S> extraInitializer) {
		ImmutableList.Builder<IPojoInitializer<? super S>> builder = ImmutableList.builder();
		ImmutableList<IPojoInitializer<? super S>> initializers = builder.add(extraInitializer).addAll(items).build();
		return new ChainingPojoInitializer<S>(initializers);
	}
	
	@Override
	public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, T instance) {
		for (IPojoInitializer<? super T> item : items) {
			item.init(lazyPojo, pojoFactory, instance);
		}
	}

	@Override
	public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, T instance) {
		for (IPojoInitializer<? super T> item : items) {
			item.destroy(lazyPojo, pojoFactory, instance);
		}
	}

}

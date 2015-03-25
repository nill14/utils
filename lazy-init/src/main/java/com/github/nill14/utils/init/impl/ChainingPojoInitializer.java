package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public class ChainingPojoInitializer implements IPojoInitializer {
	
	private final ImmutableList<IPojoInitializer> items;
	
	
	@SafeVarargs
	public ChainingPojoInitializer(IPojoInitializer... initializers) {
		items = ImmutableList.copyOf(initializers);
	}

	public ChainingPojoInitializer(ImmutableList<IPojoInitializer> initializers) {
		items = initializers;
	}
	
	public static ChainingPojoInitializer defaultInitializer() {
		return new ChainingPojoInitializer(
				new AnnotationInjectInitializer(),
				new AnnotationLifecycleInitializer(),
				new EventBusPojoInitializer());
	}
	
	/**
	 * 
	 * @param extraInitializer The first initializer to execute
	 * @return
	 */
	public ChainingPojoInitializer with(IPojoInitializer extraInitializer) {
		ImmutableList.Builder<IPojoInitializer> builder = ImmutableList.builder();
		ImmutableList<IPojoInitializer> initializers = builder.add(extraInitializer).addAll(items).build();
		return new ChainingPojoInitializer(initializers);
	}
	
	@Override
	public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
		for (IPojoInitializer item : items) {
			item.init(lazyPojo, pojoFactory, instance);
		}
	}

	@Override
	public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
		for (IPojoInitializer item : items) {
			item.destroy(lazyPojo, pojoFactory, instance);
		}
	}

}

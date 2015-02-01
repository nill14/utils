package com.github.nill14.utils.init.impl;

import java.util.Queue;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;

@SuppressWarnings("serial")
public class ChainingPojoInitializer implements IPojoInitializer<Object> {
	
	private final Queue<IPojoInitializer<Object>> items = Queues.newConcurrentLinkedQueue();
	
	
	@SuppressWarnings("unchecked")
	public ChainingPojoInitializer addInitializer(IPojoInitializer<? extends Object> initializer) {
		Preconditions.checkNotNull(initializer);
		items.add((IPojoInitializer<Object>) initializer);
		return this;
	}
	
	public ChainingPojoInitializer() {
	}

	@Override
	public void init(ILazyPojo<?> lazyPojo, Object instance) {
		items.forEach(item -> item.init(lazyPojo, instance));

	}

	@Override
	public void destroy(ILazyPojo<?> lazyPojo, Object instance) {
		items.forEach(item -> item.destroy(lazyPojo, instance));
	}

}

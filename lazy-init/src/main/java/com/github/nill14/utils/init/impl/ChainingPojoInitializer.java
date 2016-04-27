package com.github.nill14.utils.init.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public final class ChainingPojoInitializer implements IPojoInitializer {
	
	private final CopyOnWriteArrayList<IPojoInitializer> items;
	
	
	@SafeVarargs
	private ChainingPojoInitializer(IPojoInitializer... initializers) {
		items = new CopyOnWriteArrayList<IPojoInitializer>(initializers);
	}

	public ChainingPojoInitializer(List<IPojoInitializer> initializers) {
		items = new CopyOnWriteArrayList<IPojoInitializer>(initializers);
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
	 * @return self
	 */
	public ChainingPojoInitializer with(IPojoInitializer extraInitializer) {
		ImmutableList.Builder<IPojoInitializer> builder = ImmutableList.builder();
		ImmutableList<IPojoInitializer> initializers = builder.add(extraInitializer).addAll(items).build();
		return new ChainingPojoInitializer(initializers);
	}
	
	/**
	 * 
	 * @param extraInitializer The first initializer to execute
	 */
	public void insert(IPojoInitializer extraInitializer) {
		if (extraInitializer instanceof ChainingPojoInitializer) {
			throw new IllegalArgumentException();
		}
		if (!items.contains(extraInitializer)) {
			items.add(0, extraInitializer);
		}
	}
	
	public void append(IPojoInitializer extraInitializer) {
		if (extraInitializer instanceof ChainingPojoInitializer) {
			throw new IllegalArgumentException();
		}
		if (!items.contains(extraInitializer)) {
			items.add(extraInitializer);
		}
	}
	
	public void remove(IPojoInitializer extraInitializer) {
		items.remove(extraInitializer);
	}
	
	@Override
	public <T> void init(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance, CallerContext context) {
		for (IPojoInitializer item : items) {
			item.init(resolver, beanDescriptor, instance, context);
		}
	}

	@Override
	public <T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
		for (IPojoInitializer item : items) {
			item.destroy(resolver, beanDescriptor, instance);
		}
	}
	
	public List<IPojoInitializer> getItems() {
		return Collections.unmodifiableList(items);
	}

}

package com.github.nill14.utils.init.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public class ChainingPojoInitializer implements IPojoInitializer {
	
	private final CopyOnWriteArrayList<IPojoInitializer> items;
	
	
	@SafeVarargs
	public ChainingPojoInitializer(IPojoInitializer... initializers) {
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
	 * @return
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
		items.add(0, extraInitializer);
	}
	
	public void append(IPojoInitializer extraInitializer) {
		items.add(extraInitializer);
	}
	
	public void remove(IPojoInitializer extraInitializer) {
		items.remove(extraInitializer);
	}
	
	@Override
	public void init(IPropertyResolver resolver, IPojoFactory<?> pojoFactory, Object instance) {
		for (IPojoInitializer item : items) {
			item.init(resolver, pojoFactory, instance);
		}
	}

	@Override
	public void destroy(IPropertyResolver resolver, IPojoFactory<?> pojoFactory, Object instance) {
		for (IPojoInitializer item : items) {
			item.destroy(resolver, pojoFactory, instance);
		}
	}
	
	public List<IPojoInitializer> getItems() {
		return Collections.unmodifiableList(items);
	}

}

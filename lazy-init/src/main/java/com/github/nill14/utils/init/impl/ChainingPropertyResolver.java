package com.github.nill14.utils.init.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public class ChainingPropertyResolver implements IPropertyResolver {
	
	private final ChainingPojoInitializer initializer = ChainingPojoInitializer.defaultInitializer();
	private final CopyOnWriteArrayList<IPropertyResolver> items;
	
	public ChainingPropertyResolver(IPropertyResolver... resolvers) {
		items = new CopyOnWriteArrayList<IPropertyResolver>(resolvers);
	}
	
	public ChainingPropertyResolver(ImmutableList<IPropertyResolver> resolvers) {
		items = new CopyOnWriteArrayList<IPropertyResolver>(resolvers);
	}
	
	public void insert(IPropertyResolver extraResolver) {
		items.add(0, extraResolver);
	}

	public void append(IPropertyResolver extraResolver) {
		items.add(extraResolver);
	}
	
	public void remove(IPropertyResolver extraResolver) {
		items.remove(extraResolver);
	}
	
	/**
	 * 
	 * @param extraResolver The first resolver to execute
	 * @return
	 */
	public ChainingPropertyResolver with(IPropertyResolver extraResolver) {
		ImmutableList.Builder<IPropertyResolver> builder = ImmutableList.builder();
		ImmutableList<IPropertyResolver> resolvers = builder.add(extraResolver).addAll(items).build();
		return new ChainingPropertyResolver(resolvers);
	}
	
	@Override
	public Object resolve(IParameterType type) {
		for (IPropertyResolver resolver : items) {
			Object result = resolver.resolve(type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	@Override
	public IBeanInjector toBeanInjector() {
		return new BeanInjector(this);
	}
	
	@Override
	public <T> void initializeBean(IBeanDescriptor<T> beanDescriptor, Object instance) {
		initializer.init(this, beanDescriptor, instance);
	}

	@Override
	public <T> void destroyBean(IBeanDescriptor<T> beanDescriptor, Object instance) {
		initializer.destroy(this, beanDescriptor, instance);
	}
	
	@Override
	public void insertInitializer(IPojoInitializer initializer) {
		this.initializer.insert(initializer);
	}
	
	@Override
	public void appendInitializer(IPojoInitializer extraInitializer) {
		this.initializer.append(initializer);
	}
	
	
	@Override
	public List<IPojoInitializer> getInitializers() {
		return initializer.getItems();
	}
}

package com.github.nill14.utils.init.impl;

import java.util.concurrent.CopyOnWriteArrayList;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public class ChainingPropertyResolver implements IPropertyResolver {
	
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
	public Object resolve(Object pojo, IParameterType<?> type) {
		for (IPropertyResolver resolver : items) {
			Object result = resolver.resolve(pojo, type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}

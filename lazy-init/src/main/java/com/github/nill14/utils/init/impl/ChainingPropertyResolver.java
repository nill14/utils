package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public class ChainingPropertyResolver implements IPropertyResolver {
	
	private final ImmutableList<IPropertyResolver> items;
	
	public ChainingPropertyResolver(IPropertyResolver... resolvers) {
		items = ImmutableList.copyOf(resolvers);
	}
	
	public ChainingPropertyResolver(ImmutableList<IPropertyResolver> resolvers) {
		items = resolvers;
	}
	
	public ChainingPropertyResolver with(IPropertyResolver extraResolver) {
		ImmutableList.Builder<IPropertyResolver> builder = ImmutableList.builder();
		return new ChainingPropertyResolver(builder.add(extraResolver).addAll(items).build());
	}
	
	
	@Override
	public Object resolve(Object pojo, IParameterType type) {
		for (IPropertyResolver resolver : items) {
			Object result = resolver.resolve(pojo, type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}

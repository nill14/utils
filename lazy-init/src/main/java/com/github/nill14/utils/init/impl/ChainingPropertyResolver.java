package com.github.nill14.utils.init.impl;

import java.util.Deque;

import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;

@SuppressWarnings("serial")
public class ChainingPropertyResolver implements IPropertyResolver {
	
	private final Deque<IPropertyResolver> items = Queues.newLinkedBlockingDeque();
	
	
	public final ChainingPropertyResolver pushResolver(IPropertyResolver resolver) {
		Preconditions.checkNotNull(resolver);
		items.push(resolver);
		return this;
	}
	
	public ChainingPropertyResolver() {
	}

	public ChainingPropertyResolver(IPropertyResolver defaultResolver) {
		pushResolver(defaultResolver);
	}
	
	@Override
	public Object resolve(Object pojo, IType type) {
		IPropertyResolver[] array = items.stream().toArray(IPropertyResolver[]::new);
		for (IPropertyResolver resolver : array) {
			Object result = resolver.resolve(pojo, type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

}

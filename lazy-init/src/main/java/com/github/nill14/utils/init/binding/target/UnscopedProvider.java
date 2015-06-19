package com.github.nill14.utils.init.binding.target;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.ICallerContext;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;

public final class UnscopedProvider<T> implements Provider<T> {
	
	private final IPropertyResolver resolver;
	private final IPojoFactory<T> pojoFactory;
	private final ICallerContext context;

	public UnscopedProvider(IPropertyResolver resolver, IPojoFactory<T> pojoFactory, ICallerContext context) {
		this.resolver = resolver;
		this.pojoFactory = pojoFactory;
		this.context = context;
	}

	@Override
	public T get() {
		return pojoFactory.newInstance(resolver, context);
	}
	
	public IPropertyResolver getResolver() {
		return resolver;
	}
	
	public IBeanDescriptor<T> getDescriptor() {
		return pojoFactory.getDescriptor();
	}

}

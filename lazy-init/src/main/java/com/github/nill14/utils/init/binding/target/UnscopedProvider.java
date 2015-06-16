package com.github.nill14.utils.init.binding.target;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;

public final class UnscopedProvider<T> implements Provider<T> {
	
	private final IPropertyResolver resolver;
	private final IPojoFactory<T> pojoFactory;

	public UnscopedProvider(IPropertyResolver resolver, IPojoFactory<T> pojoFactory) {
		this.resolver = resolver;
		this.pojoFactory = pojoFactory;
	}

	@Override
	public T get() {
		return pojoFactory.newInstance(resolver);
	}
	
	public IPropertyResolver getResolver() {
		return resolver;
	}
	
	public IBeanDescriptor<T> getDescriptor() {
		return pojoFactory.getDescriptor();
	}

}

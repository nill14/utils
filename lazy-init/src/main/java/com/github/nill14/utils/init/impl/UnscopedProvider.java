package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.base.Preconditions;

public final class UnscopedProvider<T> implements Provider<T> {
	
	private final IPropertyResolver resolver;
	private final IPojoFactory<T> pojoFactory;
	private final CallerContext context;
	private final BindingKey<?> bindingKey;

	public UnscopedProvider(IPropertyResolver resolver, BindingKey<?> bindingKey, IPojoFactory<T> pojoFactory, CallerContext context) {
		this.resolver = Preconditions.checkNotNull(resolver);
		this.bindingKey = Preconditions.checkNotNull(bindingKey);
		this.pojoFactory = Preconditions.checkNotNull(pojoFactory);
		this.context = Preconditions.checkNotNull(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		
		if (context.isConstructing(bindingKey)) {
			return (T) context.getInstance(bindingKey); 
		
		} else {
			ConstructionContext constructionContext = context.startConstructing(bindingKey);
			try {
				T instance = pojoFactory.newInstance(resolver, context);
				constructionContext.setInstanceIfUnset(instance);
				return instance;
				
			} finally {
				constructionContext.finishConstructing();
			}
		}
	}
	
	public IPropertyResolver getResolver() {
		return resolver;
	}
	
	public IBeanDescriptor<T> getDescriptor() {
		return pojoFactory.getDescriptor();
	}

}

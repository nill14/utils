package com.github.nill14.utils.init.binding.target;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.impl.BindingTarget;

public final class UnscopedProvider<T> implements Provider<T> {
	
	private final IPropertyResolver resolver;
	private final IPojoFactory<T> pojoFactory;

	@SuppressWarnings("unchecked")
	public UnscopedProvider(IPropertyResolver resolver, BindingTarget<T> target) {
		this.resolver = resolver;
		PojoFactoryBindingTargetVisitor bindingTargetVisitor = new PojoFactoryBindingTargetVisitor(resolver); 
		pojoFactory = (IPojoFactory<T>) target.accept(bindingTargetVisitor);
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

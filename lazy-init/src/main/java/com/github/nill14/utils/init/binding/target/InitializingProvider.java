package com.github.nill14.utils.init.binding.target;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.impl.BindingTarget;

public class InitializingProvider<T> implements Provider<T> {
	
	private final IPropertyResolver resolver;
	private final BindingTarget<T> target;

	public InitializingProvider(IPropertyResolver resolver, BindingTarget<T> target) {
		this.resolver = resolver;
		this.target = target;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		PojoFactoryBindingTargetVisitor bindingTargetVisitor = new PojoFactoryBindingTargetVisitor(resolver, key -> null);
		return (T) target.accept(bindingTargetVisitor).newInstance();
	}

}

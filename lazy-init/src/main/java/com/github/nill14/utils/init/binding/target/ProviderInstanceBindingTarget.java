package com.github.nill14.utils.init.binding.target;

import javax.inject.Provider;

import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;

public class ProviderInstanceBindingTarget<T> implements BindingTarget<T> {
	
	private final Provider<T> provider;

	public ProviderInstanceBindingTarget(Provider<T> provider) {
		this.provider = provider;
	}
	
	public Provider<T> getProvider() {
		return provider;
	}
	
	@Override
	public <R> R accept(BindingTargetVisitor<R> bindingTargetVisitor) {
		return bindingTargetVisitor.visit(this);
	}

}

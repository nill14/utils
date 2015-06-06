package com.github.nill14.utils.init.binding.target;

import javax.inject.Provider;

import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.google.common.reflect.TypeToken;

public class ProviderInstanceBindingTarget<T> implements BindingTarget<T> {
	
	private final Provider<T> provider;

	public ProviderInstanceBindingTarget(Provider<T> provider) {
		this.provider = provider;
	}
	
	public Provider<T> getProvider() {
		return provider;
	}
	
	@SuppressWarnings("unchecked")
	public TypeToken<T> getProviderToken() {
		return TypeToken.of((Class<T>)provider.getClass());
	}
	
	@Override
	public <R> R accept(BindingTargetVisitor<R> bindingTargetVisitor) {
		return bindingTargetVisitor.visit(this);
	}
	

	@Override
	public String toString() {
		return String.format("ProviderInstanceBindingTarget(%s)", provider);
	}

}

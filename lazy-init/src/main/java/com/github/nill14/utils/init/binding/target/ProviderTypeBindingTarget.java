package com.github.nill14.utils.init.binding.target;

import javax.inject.Provider;

import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.google.common.reflect.TypeToken;

public class ProviderTypeBindingTarget<T> implements BindingTarget<T> {
	
	private final TypeToken<? extends Provider<? extends T>> token;

	public ProviderTypeBindingTarget(TypeToken<? extends Provider<? extends T>> token) {
		this.token = token;
	}
	
	public TypeToken<? extends Provider<? extends T>> getToken() {
		return token;
	}
	
	@Override
	public <R> R accept(BindingTargetVisitor<R> bindingTargetVisitor) {
		return bindingTargetVisitor.visit(this);
	}
	

	@Override
	public String toString() {
		return String.format("ProviderTypeBindingTarget(%s)", token.getRawType().getSimpleName());
	}

}

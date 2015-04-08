package com.github.nill14.utils.init.binding.target;

import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.google.common.reflect.TypeToken;

public class BeanTypeBindingTarget<T> implements BindingTarget<T> {
	
	private final TypeToken<T> token;

	public BeanTypeBindingTarget(TypeToken<T> token) {
		this.token = token;
	}
	
	public TypeToken<T> getToken() {
		return token;
	}
	
	@Override
	public <R> R accept(BindingTargetVisitor<R> bindingTargetVisitor) {
		return bindingTargetVisitor.visit(this);
	}

}

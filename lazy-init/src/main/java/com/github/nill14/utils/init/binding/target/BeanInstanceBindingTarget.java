package com.github.nill14.utils.init.binding.target;

import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;

public class BeanInstanceBindingTarget<T> implements BindingTarget<T> {
	
	private final T instance;

	public BeanInstanceBindingTarget(T instance) {
		this.instance = instance;
	}
	
	public T getInstance() {
		return instance;
	}
	
	@Override
	public <R> R accept(BindingTargetVisitor<R> bindingTargetVisitor) {
		return bindingTargetVisitor.visit(this);
	}

}

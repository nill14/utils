package com.github.nill14.utils.init.binding.target;

import com.github.nill14.utils.init.api.BindingType;
import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;

public class LinkedBindingTarget<T> implements BindingTarget<T> {
	
	private BindingType<T> targetType;

	public LinkedBindingTarget(BindingType<T> targetType) {
		this.targetType = targetType;
	}
	
	public BindingType<T> getBindingType() {
		return targetType;
	}
	
	@Override
	public <R> R accept(BindingTargetVisitor<R> bindingTargetVisitor) {
		return bindingTargetVisitor.visit(this);
	}

}

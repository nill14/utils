package com.github.nill14.utils.init.binding.target;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;

public class LinkedBindingTarget<T> implements BindingTarget<T> {
	
	private final BindingKey<T> targetType;

	public static <T> LinkedBindingTarget<T> create(BindingKey<T> targetType) {
		return new LinkedBindingTarget<>(targetType);
	}
	
	public LinkedBindingTarget(BindingKey<T> targetType) {
		this.targetType = targetType;
	}
	
	public BindingKey<T> getBindingKey() {
		return targetType;
	}
	
	@Override
	public <R> R accept(BindingTargetVisitor<R> bindingTargetVisitor) {
		return bindingTargetVisitor.visit(this);
	}
	

	@Override
	public String toString() {
		return String.format("LinkedBindingTarget(%s)", targetType.getRawType().getSimpleName());
	}

}

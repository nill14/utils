package com.github.nill14.utils.init.binding.impl;


public interface BindingTarget<T> {
	
	<R> R accept(BindingTargetVisitor<R> bindingTargetVisitor);

}

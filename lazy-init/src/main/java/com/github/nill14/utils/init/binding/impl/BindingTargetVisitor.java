package com.github.nill14.utils.init.binding.impl;

import com.github.nill14.utils.init.binding.target.BeanInstanceBindingTarget;
import com.github.nill14.utils.init.binding.target.BeanTypeBindingTarget;
import com.github.nill14.utils.init.binding.target.ProviderInstanceBindingTarget;
import com.github.nill14.utils.init.binding.target.ProviderTypeBindingTarget;

public interface BindingTargetVisitor<R> {

	R visit(BeanInstanceBindingTarget<?> bindingTarget);
	
	R visit(ProviderInstanceBindingTarget<?> bindingTarget);
	
	R visit(BeanTypeBindingTarget<?> bindingTarget);
	
	R visit(ProviderTypeBindingTarget<?> bindingTarget);
}

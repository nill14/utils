package com.github.nill14.utils.init.binding.impl;

import com.github.nill14.utils.init.binding.target.BeanInstanceBindingTarget;
import com.github.nill14.utils.init.binding.target.BeanTypeBindingTarget;
import com.github.nill14.utils.init.binding.target.LinkedBindingTarget;
import com.github.nill14.utils.init.binding.target.ProviderInstanceBindingTarget;
import com.github.nill14.utils.init.binding.target.ProviderTypeBindingTarget;
import com.github.nill14.utils.init.binding.target.ProvidesMethodBindingTarget;

public interface BindingTargetVisitor<R> {

	R visit(BeanInstanceBindingTarget<?> bindingTarget);
	
	R visit(ProviderInstanceBindingTarget<?> bindingTarget);
	
	R visit(BeanTypeBindingTarget<?> bindingTarget);
	
	R visit(ProviderTypeBindingTarget<?> bindingTarget);

	R visit(ProvidesMethodBindingTarget<?> bindingTarget);

	R visit(LinkedBindingTarget<?> bindingTarget);
}

package com.github.nill14.utils.init.binding.target;

import java.lang.reflect.AnnotatedElement;

import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;

public class AnnotatedElementBindingTargetVisitor implements BindingTargetVisitor<AnnotatedElement> {

	
	@Override
	public AnnotatedElement visit(BeanInstanceBindingTarget<?> bindingTarget) {
		return bindingTarget.getInstance().getClass();
	}

	@Override
	public AnnotatedElement visit(ProviderInstanceBindingTarget<?> bindingTarget) {
		return bindingTarget.getProvider().getClass();
	}

	@Override
	public AnnotatedElement visit(BeanTypeBindingTarget<?> bindingTarget) {
		return bindingTarget.getToken().getRawType();
	}

	@Override
	public AnnotatedElement visit(ProviderTypeBindingTarget<?> bindingTarget) {
		return bindingTarget.getToken().getRawType();
	}

	@Override
	public AnnotatedElement visit(ProvidesMethodBindingTarget<?> bindingTarget) {
		return bindingTarget.getMethod();
	}
	
	
	@Override
	public AnnotatedElement visit(LinkedBindingTarget<?> bindingTarget) {
		return null;
	}

}

package com.github.nill14.utils.init.binding.target;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.google.common.reflect.TypeToken;

public class ProvidesMethodBindingTarget<T> implements BindingTarget<T> {
	

	private final Method m;
	private final @Nullable Object instance;
	private final TypeToken<T> token;

	/**
	 * 
	 * @param instance Nullable instance when the method is static
	 */
	@SuppressWarnings("unchecked")
	public ProvidesMethodBindingTarget(Method m, @Nullable Object instance) {
		this.m = m;
		this.instance = instance;
		token = (TypeToken<T>) TypeToken.of(m.getReturnType());
	}
	
	public TypeToken<T> getToken() {
		return token;
	}
	
	@Override
	public <R> R accept(BindingTargetVisitor<R> bindingTargetVisitor) {
		return bindingTargetVisitor.visit(this);
	}

}

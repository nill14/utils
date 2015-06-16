package com.github.nill14.utils.init.binding.target;

import java.lang.reflect.Method;
import java.util.Collection;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.github.nill14.utils.init.inject.MethodInjectionDescriptor;
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
	
	public Object getInstance() {
		return instance;
	}
	
	public Method getMethod() {
		return m;
	}
	
	@Override
	public <R> R accept(BindingTargetVisitor<R> bindingTargetVisitor) {
		return bindingTargetVisitor.visit(this);
	}
	
	@Override
	public String toString() {
		return String.format("ProvidesMethodBindingTarget(%s)", m);
	}

}

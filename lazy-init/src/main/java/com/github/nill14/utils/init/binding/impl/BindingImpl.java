package com.github.nill14.utils.init.binding.impl;

import com.github.nill14.utils.init.api.BindingType;
import com.github.nill14.utils.init.api.IScope;
import com.google.common.base.Preconditions;

public final class BindingImpl<T> {
	
	private final BindingType<T> bindingType;
	private final BindingTarget<? extends T> target;
	private final Object source;
	private final IScope scope;

	public BindingImpl(BindingType<T> type, BindingTarget<? extends T> target, IScope scope, Object source) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(scope);
		Preconditions.checkNotNull(target);
		Preconditions.checkNotNull(source);
		this.bindingType = type;
		this.target = target;
		this.scope = scope;
		this.source = source;
	}
	

	public BindingTarget<? extends T> getBindingTarget() {
		return target;
	}
	
	public BindingType<T> getBindingType() {
		return bindingType;
	}
  
//	public TypeToken<T> getValueToken() {
//		return factory.getType();
//	}
	
	public Object getSource() {
		return source;
	}
	
	public IScope getScope() {
		return scope;
	}
	
}
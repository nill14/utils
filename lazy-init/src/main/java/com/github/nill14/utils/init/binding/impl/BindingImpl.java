package com.github.nill14.utils.init.binding.impl;

import java.lang.annotation.Annotation;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.target.LinkedBindingTarget;
import com.google.common.base.Preconditions;

public final class BindingImpl<T> {
	
	private final BindingKey<T> bindingKey;
	private final BindingTarget<? extends T> target;
	private final Object source;
	private final IScope scope;

	public BindingImpl(BindingKey<T> key, BindingTarget<? extends T> target, IScope scope, Object source) {
		Preconditions.checkNotNull(key);
		Preconditions.checkNotNull(scope);
		Preconditions.checkNotNull(target);
		Preconditions.checkNotNull(source);
		this.bindingKey = key;
		this.target = target;
		this.scope = scope;
		this.source = source;
	}
	

	public BindingTarget<? extends T> getBindingTarget() {
		return target;
	}
	
	public BindingKey<T> getBindingKey() {
		return bindingKey;
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
	

	@Override
	public String toString() {
		return String.format("Binding(%s -> %s, %s)", bindingKey, target, scope);
	}
	
	
	
	public BindingImpl<T> keyWithQualifier(Annotation qualifier) {
		return new BindingImpl<>(bindingKey.withQualifier(qualifier), target, scope, source);
	}
	
	public BindingImpl<T> withLinkedBinding(BindingKey<? extends T> targetType) {
		return new BindingImpl<>(bindingKey, LinkedBindingTarget.create(targetType), scope, source);
	}
	
	public BindingImpl<T> withScope(IScope scope) {
		return new BindingImpl<>(bindingKey, target, scope, source);
	}	
	
}
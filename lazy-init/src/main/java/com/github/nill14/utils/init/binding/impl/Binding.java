package com.github.nill14.utils.init.binding.impl;

import java.lang.annotation.Annotation;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.binding.target.LinkedBindingTarget;
import com.github.nill14.utils.init.scope.IScopeStrategy;
import com.google.common.base.Preconditions;

public final class Binding<T> {
	
	private final BindingKey<T> bindingKey;
	private final BindingTarget<? extends T> target;
	private final Object source;
	private final IScopeStrategy scope;

	public Binding(BindingKey<T> key, BindingTarget<? extends T> target, IScopeStrategy scope, Object source) {
		this.bindingKey = Preconditions.checkNotNull(key);
		this.target = Preconditions.checkNotNull(target);
		this.scope = Preconditions.checkNotNull(scope);
		this.source = Preconditions.checkNotNull(source);
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
	
	public IScopeStrategy getScopeStrategy() {
		return scope;
	}
	

	@Override
	public String toString() {
		return String.format("Binding(%s -> %s, %s)", bindingKey, target, scope);
	}
	
	
	
	public Binding<T> keyWithQualifier(Annotation qualifier) {
		return new Binding<>(bindingKey.withQualifier(qualifier), target, scope, source);
	}
	
	public Binding<T> withLinkedTarget(BindingKey<? extends T> targetType) {
		return new Binding<>(bindingKey, LinkedBindingTarget.create(targetType), scope, source);
	}
	
	public Binding<T> withScopeStrategy(IScopeStrategy scope) {
		return new Binding<>(bindingKey, target, scope, source);
	}	
	
}
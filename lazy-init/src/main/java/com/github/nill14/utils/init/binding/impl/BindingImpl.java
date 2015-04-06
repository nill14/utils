package com.github.nill14.utils.init.binding.impl;

import java.lang.annotation.Annotation;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

public final class BindingImpl<T> {
	
	private final TypeToken<T> token;
	private final Set<Annotation> qualifiers;
	private final BindingTarget<? extends T> target;
	private final Object source;

	public BindingImpl(TypeToken<T> token, Set<Annotation> qualifiers, BindingTarget<? extends T> target, Object source) {
		Preconditions.checkNotNull(token);
		Preconditions.checkNotNull(qualifiers);
		Preconditions.checkNotNull(target);
		Preconditions.checkNotNull(source);
		this.token = token;
		this.qualifiers = ImmutableSet.copyOf(qualifiers);
		this.target = target;
		this.source = source;
	}
	

	public BindingTarget<? extends T> getBindingTarget() {
		return target;
	}
	
	
	public Set<Annotation> getQualifiers() {
		return qualifiers;
	}
	
	public TypeToken<T> getKeyToken() {
		return token;
	}
  
//	public TypeToken<T> getValueToken() {
//		return factory.getType();
//	}
	
	public Object getSource() {
		return source;
	}
	
}
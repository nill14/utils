package com.github.nill14.utils.init.impl;

import java.util.Deque;
import java.util.Map;

import com.github.nill14.utils.annotation.Experimental;
import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IScope;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.reflect.TypeToken;

public final class CallerContext {

	private final Map<BindingKey<?>, ConstructionContext> constructions = Maps.newHashMap(); 
	
	private final Deque<ConstructionContext> stack = Queues.newArrayDeque();

	private final Object externalContext;
	
	public CallerContext(Object externalContext) {
		this.externalContext = Preconditions.checkNotNull(externalContext);
	}
	
	@Experimental
	@Deprecated
	public IScope resolveScope(IScope scope) {
		return scope;
	}

	public boolean isConstructing(BindingKey<?> bindingKey) {
		return constructions.containsKey(bindingKey);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getInstance(BindingKey<?> bindingKey) {
		return constructions.get(bindingKey).getInstance((Class<T>) bindingKey.getRawType());
	}
	
	public ConstructionContext startConstructing(BindingKey<?> bindingKey) {
		ConstructionContext constructionContext = new ConstructionContext(this, bindingKey);
		stack.push(constructionContext);
		ConstructionContext put = constructions.put(bindingKey, constructionContext);
		if (put != null) {
			throw new IllegalStateException();
		}
		
		return constructionContext;
	}
	
	public ConstructionContext stopConstructing(BindingKey<?> bindingkey) {
//		constructions.remove(descriptor);
		ConstructionContext constructionContext = stack.pop();
		if (!constructionContext.getToken().isAssignableFrom(bindingkey.getToken())) {
			throw new IllegalStateException();
		}
		return null;
	}
	
	public void setSemiConstructedInstance(TypeToken<?> token, Object instance) {
		ConstructionContext constructionContext = stack.peek();
		if (constructionContext != null && constructionContext.getToken().isAssignableFrom(token)) {
			constructionContext.setInstance(instance);
		}
	}

	public Object getExternalContext() {
		return externalContext;
	}

	public static CallerContext prototype() {
		return new CallerContext(Thread.currentThread());
	}
	
}

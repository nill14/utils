package com.github.nill14.utils.init.impl;

import java.util.Deque;
import java.util.Map;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IScopeContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.reflect.TypeToken;

public final class CallerContext {

	private final Map<BindingKey<?>, ConstructionContext> constructions = Maps.newHashMap(); 
	
	private final Deque<ConstructionContext> stack = Queues.newArrayDeque();

	private final IScopeContext scopeContext;
	
	public CallerContext(IScopeContext scopeContext) {
		this.scopeContext = Preconditions.checkNotNull(scopeContext);
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

	public IScopeContext getScopeContext() {
		return scopeContext;
	}

	public static CallerContext prototype() {
		return new CallerContext(IScopeContext.none());
	}
	
}

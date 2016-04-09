package com.github.nill14.utils.init.scope;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.api.IScopeContext;

public abstract class AbstractThreadScope implements IScope {

	private final ThreadLocal<ScopeContext> threadLocal = ThreadLocal.withInitial(ScopeContext::new);
	

	@Override
	public <T> Provider<T> scope(BindingKey<T> type, Provider<T> unscoped, IScopeContext scopeContext) {
		return threadLocal.get().scope(type, unscoped);
	}
	
	
	protected ScopeContext get() {
		return threadLocal.get();
	}
	
	protected void set(ScopeContext context) {
		threadLocal.set(context);
	}
	
	protected void remove() {
		threadLocal.remove();
	}
	
	protected <T> Provider<T> outOfScopeProvider() {
		return () -> null;
	}
}

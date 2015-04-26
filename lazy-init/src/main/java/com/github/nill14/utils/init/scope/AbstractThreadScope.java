package com.github.nill14.utils.init.scope;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingType;
import com.github.nill14.utils.init.api.IScope;

public abstract class AbstractThreadScope implements IScope {

	private final ThreadLocal<ScopeContext> threadLocal = ThreadLocal.withInitial(ScopeContext::new);
	

	@Override
	public <T> Provider<T> scope(BindingType<T> type, Provider<T> unscoped) {
		return threadLocal.get().scope(type, unscoped);
	}
	
	
	public ScopeContext get() {
		return threadLocal.get();
	}
	
	public void set(ScopeContext context) {
		threadLocal.set(context);
	}
	
	public void remove() {
		threadLocal.remove();
	}
}

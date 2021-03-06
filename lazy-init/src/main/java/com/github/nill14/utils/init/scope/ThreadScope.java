package com.github.nill14.utils.init.scope;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.api.IScopeContext;

public class ThreadScope implements IScope {

	private final ThreadLocal<ScopeContext> threadLocal = ThreadLocal.withInitial(ScopeContext::new);
	
	
	private ThreadScope() {
	}

	@Override
	public <T> Provider<T> scope(BindingKey<T> type, Provider<T> unscoped, IScopeContext scopeContext) {
		return threadLocal.get().scope(type, unscoped);
	}
	
	
	
	private static final ThreadScope INSTANCE = new ThreadScope();
	public static final ThreadScope instance() {
		return INSTANCE;
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

package com.github.nill14.utils.init.scope;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.api.IScopeContext;

public class RequestScope implements IScope {

	private final ThreadLocal<ScopeContext> threadLocal = ThreadLocal.withInitial(ScopeContext::new);
	
	
	private RequestScope() {
	}

	@Override
	public <T> Provider<T> scope(BindingKey<T> type, Provider<T> unscoped, IScopeContext scopeContext) {
		return threadLocal.get().scope(type, unscoped);
	}
	
	
	
	private static final RequestScope INSTANCE = new RequestScope();
	public static final RequestScope instance() {
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
	
	public void start() {
		if (threadLocal.get() != null) {
			throw new IllegalStateException("Cannot start new request scope when another is active.");
		}
		
		threadLocal.remove();
		threadLocal.get(); //sets the initial value
	}
	
	public void stop() {
		ScopeContext scopeContext = threadLocal.get();
		if (scopeContext == null) {
			throw new IllegalStateException("Cannot stop the scope when any isn't active.");
		}
		threadLocal.remove();
		scopeContext.terminate();
	}
	
}

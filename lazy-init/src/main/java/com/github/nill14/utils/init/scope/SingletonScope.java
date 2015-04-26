package com.github.nill14.utils.init.scope;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingType;
import com.github.nill14.utils.init.api.IScope;

public class SingletonScope implements IScope {

	private final ScopeContext context = new ScopeContext();
	
	private SingletonScope() {
	}

	@Override
	public <T> Provider<T> scope(BindingType<T> type, Provider<T> unscoped) {
		return context.scope(type, unscoped);
	}
	
	
	
	private static final SingletonScope INSTANCE = new SingletonScope();
	public static final SingletonScope instance() {
		return INSTANCE;
	}

	
}

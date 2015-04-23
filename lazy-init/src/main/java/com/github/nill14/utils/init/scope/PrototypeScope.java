package com.github.nill14.utils.init.scope;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IScope;

public class PrototypeScope implements IScope {
	
	private PrototypeScope() {
	}

	@Override
	public <T> Provider<T> scope(IParameterType type, Provider<T> unscoped) {
		return unscoped;
	}
	
	
	
	private static final PrototypeScope INSTANCE = new PrototypeScope();
	public static final PrototypeScope instance() {
		return INSTANCE;
	}

}

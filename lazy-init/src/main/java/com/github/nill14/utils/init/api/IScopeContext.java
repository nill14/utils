package com.github.nill14.utils.init.api;

import javax.inject.Provider;

public interface IScopeContext {
	
	static IScopeContext none() {
		return new IScopeContext() {

			@Override
			public <T> Provider<T> scope(BindingKey<T> type, Provider<T> unscoped) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean isSupported() {
				return false;
			}
		};
	}
	
	default boolean isSupported() {
		return true;
	}

	<T> Provider<T> scope(BindingKey<T> type, Provider<T> unscoped);

}




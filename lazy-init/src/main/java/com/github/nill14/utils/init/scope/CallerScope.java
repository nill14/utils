package com.github.nill14.utils.init.scope;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.api.IScopeContext;

/**
 * 
 * A scope delegating the scoping onto the calling facility (e.g. a ISession or so)
 *
 */
public class CallerScope implements IScope {

	@Override
	public <T> Provider<T> scope(BindingKey<T> bindingKey, Provider<T> unscoped, IScopeContext scopeContext) {
		return scopeContext.scope(bindingKey, unscoped);
	}

}

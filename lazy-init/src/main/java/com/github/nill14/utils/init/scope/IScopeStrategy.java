package com.github.nill14.utils.init.scope;

import com.github.nill14.utils.init.api.IScope;

public interface IScopeStrategy {

	IScope resolveScope();
	
	boolean isPrototype();
	
	boolean isSingleton();
	
	/**
	 * 
	 * @param obj
	 * @return true when two scope strategies resolve into the same scope
	 */
	boolean scopeEquals(IScopeStrategy obj);
	
	
}

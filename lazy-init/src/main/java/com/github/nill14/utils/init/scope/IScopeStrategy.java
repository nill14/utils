package com.github.nill14.utils.init.scope;

import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.impl.CallerContext;

public interface IScopeStrategy {

	IScope resolveScope(CallerContext context);
	
	boolean isPrototype();
	
	boolean isSingleton();
	
	/**
	 * 
	 * @param obj
	 * @return true when two scope strategies resolve into the same scope
	 */
	boolean scopeEquals(IScopeStrategy obj);
	
	
}

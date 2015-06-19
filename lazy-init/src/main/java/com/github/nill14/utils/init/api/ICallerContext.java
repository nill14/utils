package com.github.nill14.utils.init.api;

import com.github.nill14.utils.init.scope.PrototypeScope;

public interface ICallerContext {
	IScope resolveScope(IScope scope);

	
	public static ICallerContext prototype() {
		return new ICallerContext() {
			
			@Override
			public IScope resolveScope(IScope scope) {
				return PrototypeScope.instance();
			}
		};
	}
}

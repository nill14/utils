package com.github.nill14.utils.init.api;

public interface ICallerContext {
	IScope resolveScope(IScope scope);

	
	public static ICallerContext prototype() {
		return new ICallerContext() {
			
			@Override
			public IScope resolveScope(IScope scope) {
				return scope;
			}
		};
	}
}

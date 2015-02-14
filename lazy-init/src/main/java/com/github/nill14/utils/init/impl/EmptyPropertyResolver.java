package com.github.nill14.utils.init.impl;

import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IType;

@SuppressWarnings("serial")
public final class EmptyPropertyResolver implements IPropertyResolver {

	public static final EmptyPropertyResolver instance = new EmptyPropertyResolver();
	public static EmptyPropertyResolver empty() {
		return instance;
	}
	
	
	private EmptyPropertyResolver() {
		
	}
	
	@Override
	public Object resolve(Object pojo, IType type) {
		// TODO Auto-generated method stub
		return null;
	}

}

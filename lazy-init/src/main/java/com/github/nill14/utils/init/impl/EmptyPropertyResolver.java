package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;

@SuppressWarnings("serial")
public final class EmptyPropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {

	public static final EmptyPropertyResolver instance = new EmptyPropertyResolver();
	public static EmptyPropertyResolver empty() {
		return instance;
	}
	
	
	private EmptyPropertyResolver() {
		
	}
	
	@Override
	protected Object findByName(String name, IParameterType type) {
		return null;
	}


	@Override
	protected Object findByType(IParameterType type) {
		return null;
	}


	@Override
	protected Collection<?> findAllByType(IParameterType type) {
		return Collections.emptyList();
	}


	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier) {
		return null;
	}



}

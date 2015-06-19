package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.github.nill14.utils.init.api.ICallerContext;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;

@SuppressWarnings("serial")
public final class EmptyPropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {

	public static EmptyPropertyResolver empty() {
		return new EmptyPropertyResolver();
	}
	
	private EmptyPropertyResolver() {
	}
	
	@Override
	protected Object findByName(String name, IParameterType type, ICallerContext context) {
		return null;
	}


	@Override
	protected Object findByType(IParameterType type, ICallerContext context) {
		return null;
	}


	@Override
	protected Collection<?> findAllByType(IParameterType type, ICallerContext context) {
		return Collections.emptyList();
	}


	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier, ICallerContext context) {
		return null;
	}
	
	@Override
	public void insertInitializer(IPojoInitializer initializer) {
		super.insertInitializer(initializer);
	}
	
	@Override
	public void appendInitializer(IPojoInitializer extraInitializer) {
		super.appendInitializer(extraInitializer);
	}
	

}

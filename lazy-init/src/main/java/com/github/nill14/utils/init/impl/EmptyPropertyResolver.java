package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;

@SuppressWarnings("serial")
public final class EmptyPropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {

	public static EmptyPropertyResolver empty() {
		return new EmptyPropertyResolver(IPojoInitializer.standard());
	}
	
	public static EmptyPropertyResolver empty(IPojoInitializer pojoInitializer) {
		return new EmptyPropertyResolver(pojoInitializer);
	}
	
	private EmptyPropertyResolver(IPojoInitializer pojoInitializer) {
		insertInitializer(pojoInitializer);
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
	
	public void insertInitializer(IPojoInitializer initializer) {
		super.insertInitializer(initializer);
	}
	
	public void appendInitializer(IPojoInitializer extraInitializer) {
		super.appendInitializer(extraInitializer);
	}
	


}

package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Provider;

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
	protected Provider<?> findByName(Object pojo, String name, Class<?> type) {
		return nullProvider();
	}


	@Override
	protected Provider<?> findByType(Object pojo, IParameterType<?> type, Class<?> clazz) {
		return nullProvider();
	}


	@Override
	protected Collection<?> findAllByType(Object pojo, Class<?> type) {
		return Collections.emptyList();
	}


	@Override
	protected Provider<?> findByQualifier(Object pojo, Class<?> type, Annotation qualifier) {
		return nullProvider();
	}



}

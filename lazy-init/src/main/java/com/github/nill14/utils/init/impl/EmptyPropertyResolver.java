package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;

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
	protected Object findByName(Object pojo, String name, Class<?> type) {
		return null;
	}


	@Override
	protected Object findByType(Object pojo, Class<?> type) {
		return null;
	}


	@Override
	protected Collection<?> findAllByType(Object pojo, Class<?> type) {
		return null;
	}


	@Override
	protected Object findByQualifier(Object pojo, Class<?> type, Annotation qualifier,
			Iterator<? extends Annotation> nextQualifiers) {
		return null;
	}

}

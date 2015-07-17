package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import org.mockito.Mockito;

import com.github.nill14.utils.init.api.IParameterType;

public class MockitoFallbackResolver extends AbstractPropertyResolver {

	private static final long serialVersionUID = 1L;

	@Override
	public Object findByType(IParameterType type, CallerContext context) {
		return Mockito.mock(type.getRawType(), Mockito.RETURNS_DEEP_STUBS);
	}

	@Override
	protected Object findByName(String name, IParameterType type, CallerContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<?> findAllByType(IParameterType type, CallerContext context) {
		return Collections.emptyList();
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier, CallerContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	
}

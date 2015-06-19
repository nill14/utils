package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import org.mockito.Mockito;

import com.github.nill14.utils.init.api.ICallerContext;
import com.github.nill14.utils.init.api.IParameterType;

public class MockitoFallbackResolver extends AbstractPropertyResolver {

	private static final long serialVersionUID = 1L;

	@Override
	public Object findByType(IParameterType type, ICallerContext context) {
		return Mockito.mock(type.getRawType(), Mockito.RETURNS_DEEP_STUBS);
	}

	@Override
	protected Object findByName(String name, IParameterType type, ICallerContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<?> findAllByType(IParameterType type, ICallerContext context) {
		return Collections.emptyList();
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier, ICallerContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	
}

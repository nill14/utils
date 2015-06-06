package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import org.mockito.Mockito;

import com.github.nill14.utils.init.api.IParameterType;

public class MockitoFallbackResolver extends AbstractPropertyResolver {

	private static final long serialVersionUID = 1L;

	@Override
	public Object findByType(IParameterType type) {
		return Mockito.mock(type.getRawType(), Mockito.RETURNS_DEEP_STUBS);
	}

	@Override
	protected Object findByName(String name, IParameterType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<?> findAllByType(IParameterType type) {
		return Collections.emptyList();
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier) {
		// TODO Auto-generated method stub
		return null;
	}

	
}

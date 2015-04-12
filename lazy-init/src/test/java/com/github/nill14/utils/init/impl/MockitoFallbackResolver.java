package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import org.mockito.Mockito;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class MockitoFallbackResolver implements IPropertyResolver {

	private static final long serialVersionUID = 1L;

	@Override
	public Provider<?> resolve(Object pojo, IParameterType<?> type) {
		return () -> Mockito.mock(type.getRawType(), Mockito.RETURNS_DEEP_STUBS);
	}

}

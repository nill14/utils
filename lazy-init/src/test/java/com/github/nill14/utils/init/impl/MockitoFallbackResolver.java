package com.github.nill14.utils.init.impl;

import org.mockito.Mockito;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class MockitoFallbackResolver implements IPropertyResolver {

	private static final long serialVersionUID = 1L;

	@Override
	public Object resolve(IParameterType<?> type) {
		return Mockito.mock(type.getRawType(), Mockito.RETURNS_DEEP_STUBS);
	}

}

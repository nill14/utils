package com.github.nill14.utils.init.impl;

import java.util.Collections;
import java.util.List;

import org.mockito.Mockito;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class MockitoFallbackResolver implements IPropertyResolver {

	private static final long serialVersionUID = 1L;

	@Override
	public Object resolve(IParameterType type) {
		return Mockito.mock(type.getRawType(), Mockito.RETURNS_DEEP_STUBS);
	}

	@Override
	public void initializeBean(Object instance) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public IBeanInjector toBeanInjector() {
		return new BeanInjector(this);
	}

	@Override
	public List<IPojoInitializer> getInitializers() {
		return Collections.emptyList();
	}
	
}

package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.github.nill14.utils.init.inject.ReflectionUtils;
import com.google.common.reflect.TypeToken;

public class PojoProviderFactory<T> implements IPojoFactory<T> {

	@SuppressWarnings("unchecked")
	public static <T> IPojoFactory<T> singleton(T singleton, IPropertyResolver resolver) {
		return new PojoProviderFactory<T>((TypeToken<T>) TypeToken.of(singleton.getClass()), () -> singleton, resolver);
	}
	
	public static <T> PojoProviderFactory<T> nullFactory(Class<T> nullType) {
		return new PojoProviderFactory<T>(TypeToken.of(nullType), () -> null, IPropertyResolver.empty());
	}

	private static final long serialVersionUID = 1L;	
	private final TypeToken<T> typeToken;
	private final Provider<T> provider;
	private final IPropertyResolver resolver;
	
    /** Cache the beanDescriptor */
    private IBeanDescriptor<T> beanDescriptor; 

	public PojoProviderFactory(Provider<T> provider, IPropertyResolver resolver) {
		this.typeToken = ReflectionUtils.getProviderReturnTypeToken(provider);
		this.resolver = resolver;
		this.provider = provider;
	}
	
	protected PojoProviderFactory(TypeToken<T> typeToken, Provider<T> provider, IPropertyResolver resolver) {
		this.typeToken = typeToken;
		this.resolver = resolver;
		this.provider = provider;
	}
	
	@Override
	public T newInstance() {
		return provider.get();
	}

	@Override
	public TypeToken<T> getType() {
		return typeToken;
	}

	@Override
	public IPropertyResolver getResolver() {
		return resolver;
	}
	
	
	@Override
	public IBeanDescriptor<T> getDescriptor() {
		//avoiding synchronization on purpose
		IBeanDescriptor<T> h = beanDescriptor;
		if (h == null) {
			h = new PojoInjectionDescriptor<>(typeToken);
			beanDescriptor = h;
		}
		return h;
	}
	
}

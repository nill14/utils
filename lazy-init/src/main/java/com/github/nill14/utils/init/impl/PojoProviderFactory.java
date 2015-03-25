package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.reflect.TypeToken;

public class PojoProviderFactory<T> implements IPojoFactory<T> {

	@SuppressWarnings("unchecked")
	public static <T> IPojoFactory<T> singleton(T singleton, IPropertyResolver resolver) {
		TypeToken<T> token = (TypeToken<T>) TypeToken.of(singleton.getClass());
		return new PojoProviderFactory<T>(token, () -> singleton, resolver);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> IPojoFactory<T> create(Provider<T> provider, IPropertyResolver resolver) {
		TypeToken<T> token = getProviderReturnTypeToken((Class<Provider<T>>) provider.getClass());
		return new PojoProviderFactory<T>(token, provider, resolver);
	}
	
	public static <T> PojoProviderFactory<T> nullFactory(Class<T> nullType) {
		return new PojoProviderFactory<T>(TypeToken.of(nullType), () -> null, IPropertyResolver.empty());
	}
	
	@SuppressWarnings({ "unchecked" })
	/*package*/ static <T> TypeToken<T> getProviderReturnTypeToken(Class<? extends Provider<? extends T>> providerClass) {
		try {
			return (TypeToken<T>) TypeToken.of(providerClass.getMethod("get").getGenericReturnType());
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
	private final TypeToken<T> typeToken;
	private final Provider<T> provider;
	private final IBeanDescriptor<T> beanDescriptor;
	private final IPropertyResolver resolver;

	private PojoProviderFactory(TypeToken<T> typeToken, Provider<T> provider, IPropertyResolver resolver) {
		this.typeToken = typeToken;
		this.resolver = resolver;
		beanDescriptor = new PojoInjectionDescriptor<T>(typeToken);
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
		return beanDescriptor;
	}

	private static final long serialVersionUID = 1L;
}

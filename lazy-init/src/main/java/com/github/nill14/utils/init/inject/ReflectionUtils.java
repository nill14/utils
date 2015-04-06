package com.github.nill14.utils.init.inject;

import javax.inject.Provider;

import com.google.common.reflect.TypeToken;

public enum ReflectionUtils {
	;
	
	@SuppressWarnings({ "unchecked" })
	public static <T> TypeToken<T> getProviderReturnTypeToken(Class<? extends Provider<? extends T>> providerClass) {
		try {
			return (TypeToken<T>) TypeToken.of(providerClass.getMethod("get").getGenericReturnType());
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static <T> TypeToken<T> getProviderReturnTypeToken(Provider<? extends T> provider) {
		return getProviderReturnTypeToken((Class<? extends Provider<? extends T>>) provider.getClass());
	}
	
	@SuppressWarnings({ "unchecked" })
	public static <T> TypeToken<T> getProviderReturnTypeToken(TypeToken<? extends Provider<? extends T>> providerType) {
		return getProviderReturnTypeToken((Class<? extends Provider<? extends T>>)providerType.getRawType());
	}
}

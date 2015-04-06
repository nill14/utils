package com.github.nill14.utils.init.binding;

import javax.inject.Provider;

import com.google.common.reflect.TypeToken;

public interface LinkedBindingBuilder<T> extends ScopedBindingBuilder {

	ScopedBindingBuilder to(Class<? extends T> implementation);

	ScopedBindingBuilder to(TypeToken<? extends T> implementation);

	/**
	 *
	 * @see com.github.nill14.utils.init.api.IBeanInjector#injectMembers(Object)
	 */
	void toInstance(T instance);


	/**
	 *
	 * @see com.github.nill14.utils.init.api.IBeanInjector#injectMembers(Object)
	 */
	ScopedBindingBuilder toProvider(Provider<? extends T> provider);

	ScopedBindingBuilder toProvider(Class<? extends Provider<? extends T>> providerType);

	ScopedBindingBuilder toProvider(TypeToken<? extends Provider<? extends T>> providerType);

}

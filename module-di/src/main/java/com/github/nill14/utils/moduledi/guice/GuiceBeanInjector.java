package com.github.nill14.utils.moduledi.guice;

import com.github.nill14.utils.init.api.BindingType;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

@SuppressWarnings("unchecked")
public class GuiceBeanInjector implements IBeanInjector {

	private final Injector injector;
	
	public GuiceBeanInjector(Injector injector) {
		this.injector = injector;
	}

	@Override
	public void injectMembers(Object bean) {
		injector.injectMembers(bean);
	}
	
	@Override
	public <T> T wire(Class<T> beanClass) {
		return injector.getInstance(beanClass);
	}

	@Override
	public <T> T wire(TypeToken<T> typeToken) {
		return (T) injector.getInstance(Key.get(TypeLiteral.get(typeToken.getType())));
	}
	
	@Override
	public <T> T wire(BindingType<T> type) {
		return (T) injector.getInstance(Key.get(type.getGenericType(), type.getQualifier()));
	}



}

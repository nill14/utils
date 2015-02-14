package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;

public final class PojoFactory<T> implements IPojoFactory<T> {
	
	private static final long serialVersionUID = -8524486418807436934L;

	public static <T> IPojoFactory<T> create(Class<T> beanClass) {
		return new PojoFactory<>(beanClass, EmptyPropertyResolver.empty());
	}

	private final Class<T> beanClass;
	private final IPropertyResolver resolver;
	
	private PojoFactory(Class<T> beanClass, IPropertyResolver resolver) {
		this.beanClass = beanClass;
		this.resolver = resolver;
	}

	@Override
	public T newInstance() {
		try {
			return beanClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<T> getType() {
		return beanClass;
	}
	
	
	private Constructor<?> findConstructor() throws NoSuchMethodException, SecurityException {

		Optional<Constructor<?>> optional = Stream.of(beanClass.getDeclaredConstructors())
				.filter(c -> c.isAnnotationPresent(Inject.class)).findFirst();
		
		if (optional.isPresent()) {
			return optional.get();
		}

		return beanClass.getConstructor();
	}
	
	private void invokeConstructor() throws NoSuchMethodException, SecurityException {
		Constructor<?> constructor = findConstructor();
		
//		constructor.getAnnotatedParameterTypes()
	}

}

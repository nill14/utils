package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class AnnotationPojoInitializer implements IPojoInitializer<Object> {

	public static IPojoInitializer<Object> withResolver(IPropertyResolver resolver) {
		return new AnnotationPojoInitializer(resolver);
	}
	
	private final IPropertyResolver resolver;

	private AnnotationPojoInitializer(IPropertyResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public void init(Object instance) {
		doInject(instance);
		
		doPostConstruct(instance);
	}

	@Override
	public void destroy(Object instance) {
		doPreDestroy(instance);
		
	}

	private Stream<Field> getFields(Object instance) {
		Field[] fields = instance.getClass().getDeclaredFields();
		return Stream.of(fields)
				.filter(f -> !Modifier.isStatic(f.getModifiers()))
				.filter(f -> f.isAnnotationPresent(Inject.class));
	}	
	
	private Optional<Method> getMethod(Object instance, Class<? extends Annotation> annotationClass) {
		Method[] methods = instance.getClass().getDeclaredMethods();
		return Stream.of(methods)
				.filter(m -> !Modifier.isStatic(m.getModifiers()))
				.filter(m -> m.isAnnotationPresent(annotationClass))
				.findFirst();
	}	
	
	private Object invoke(Object instance, Method method, Object[] args) {
		try {
			return method.invoke(instance, args);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		}
	}
	
	private void doPreDestroy(Object instance) {
		Optional<Method> optional = getMethod(instance, PreDestroy.class);
		if (optional.isPresent()) {
			Method method = optional.get();
			method.setAccessible(true);
			invoke(instance, method, null);
		}
	}

	private void doPostConstruct(Object instance) {
		Optional<Method> optional = getMethod(instance, PostConstruct.class);
		if (optional.isPresent()) {
			Method method = optional.get();
			method.setAccessible(true);
			invoke(instance, method, null);
		}
	}
	
	private void doInject(Object instance) {
		getFields(instance).forEach(f -> {
			f.setAccessible(true);
			Object value = resolver.resolve(instance, f.getType(), f.getName());
			try {
				f.set(instance, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}

package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.FieldInjectionDescriptor;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;

public class AnnotationPojoInitializer implements IPojoInitializer<Object> {

	private static final long serialVersionUID = -6999206469201978450L;

	public static AnnotationPojoInitializer withResolver(IPropertyResolver resolver) {
		return new AnnotationPojoInitializer(resolver);
	}
	
	private final IPropertyResolver resolver;

	private AnnotationPojoInitializer(IPropertyResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public void init(ILazyPojo<?> lazyPojo, Object instance) {
		doInject(instance);
		
		doPostConstruct(instance);
	}

	@Override
	public void destroy(ILazyPojo<?> lazyPojo, Object instance) {
		doPreDestroy(instance);
		
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
		PojoInjectionDescriptor injector = new PojoInjectionDescriptor(instance.getClass());
		for (FieldInjectionDescriptor fd : injector.getFieldDescriptors()) {
			Object value = resolver.resolve(instance, fd);
			if (value != null) {
				fd.inject(instance, value);
				
			} else if (!fd.getAnnotation(Nullable.class).isPresent()){ 
				throw new RuntimeException(String.format(
						"Cannot resolve property %s", fd));
				
			}
		}
	}
	
	

}

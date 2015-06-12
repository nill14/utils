package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.ReflectionUtils;

@SuppressWarnings("serial")
public final class AnnotationLifecycleInitializer implements IPojoInitializer {

	@Override
	public <T> void init(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
		doPostConstruct(instance);
	}

	@Override
	public <T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
		doPreDestroy(instance);
		
	}
	
	private Optional<Method> getMethod(Object instance, Class<? extends Annotation> annotationClass) {
		return ReflectionUtils.getInstanceMethods(instance.getClass())
			.filter(m -> m.isAnnotationPresent(annotationClass))
			.findFirst();
	}	
	
	private Object invoke(Object instance, Method method, Object[] args) {
		try {
			return method.invoke(instance, args);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(String.format("Exception in %s", method), e.getTargetException());
		}
	}
	
	private void doPreDestroy(Object instance) {
		Optional<Method> optional = getMethod(instance, PreDestroy.class);
		if (optional.isPresent()) {
			Method method = optional.get();
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			if (method.getParameterCount() != 0) {
				throw new RuntimeException(method + " must not have any parameters. (PreDestroy annotated)");
			}
			invoke(instance, method, null);
		}
	}

	private void doPostConstruct(Object instance) {
		Optional<Method> optional = getMethod(instance, PostConstruct.class);
		if (optional.isPresent()) {
			Method method = optional.get();
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			if (method.getParameterCount() != 0) {
				throw new RuntimeException(method + " must not have any parameters. (PostConstruct annotated)");
			}
			invoke(instance, method, null);
		}
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AnnotationLifecycleInitializer;
	}	

}

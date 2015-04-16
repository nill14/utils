package com.github.nill14.utils.init.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class ConstructorInjectionDescriptor implements IMemberDescriptor {

	private final Constructor<?> constructor;
	private final ImmutableList<IParameterType> parameterTypes;
	private final ImmutableMap<Class<? extends Annotation>, Annotation> annotations;

	public ConstructorInjectionDescriptor(Constructor<?> c) {
		constructor = c;
		
		Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
		Type[] paramTypes = constructor.getGenericParameterTypes();
		Builder<IParameterType> builder = ImmutableList.builder();
		Class<?> declaringClass = constructor.getDeclaringClass();
		
		for (int i = 0; i < constructor.getParameterCount(); i++) {
			builder.add(ParameterTypeInjectionDescriptor.of(paramTypes[i], paramAnnotations[i], constructor, declaringClass));
		}
		
		parameterTypes = builder.build();
		
		this.annotations = ImmutableMap.copyOf(AnnotationScanner.indexAnnotations(constructor.getAnnotations()));
	}

	
	@Override
	public Collection<IParameterType> getParameterTypes() {
		return parameterTypes;
	}
	
	@Override
	public Optional<Annotation> getAnnotation(Class<? extends Annotation> annotation) {
		return Optional.ofNullable(annotations.get(annotation));
	}

	@Override
	public Collection<Annotation> getAnnotations() {
		return annotations.values();
	}
	
	@Override
	public Object invoke(Object receiver, Object... args) throws ReflectiveOperationException, InvocationTargetException {
		if (!constructor.isAccessible()) {
			constructor.setAccessible(true);
		}
		return constructor.newInstance(args);
	}
	
	@Override
	public boolean isOptionalInject() {
		return false;
	}
	
	@Override
	public String toString() {
		return constructor.toGenericString();
	}

}

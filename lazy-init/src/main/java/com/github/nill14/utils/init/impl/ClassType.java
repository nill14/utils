package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import javax.inject.Qualifier;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.google.common.collect.ImmutableMap;

public class ClassType implements IParameterType {
	
	private final Class<?> clazz;
	private final ImmutableMap<Class<? extends Annotation>, Annotation> qualifiers;
	private final ImmutableMap<Class<? extends Annotation>, Annotation> annotations;

	public ClassType(Class<?> clazz) {
		this.clazz = clazz;
		qualifiers = ImmutableMap.copyOf(AnnotationScanner.findAnnotations(clazz.getAnnotations(), Qualifier.class));
		annotations = ImmutableMap.copyOf(AnnotationScanner.indexAnnotations(clazz.getAnnotations()));
	}

	@Override
	public boolean isParametrized() {
		return false;
	}

	@Override
	public Type[] getParameterTypes() {
		throw new IllegalStateException();
	}

	@Override
	public Class<?> getRawType() {
		return clazz;
	}

	@Override
	public Type getGenericType() {
		return clazz;
	}

	@Override
	public Class<?> getFirstParamClass() {
		throw new IllegalStateException();
	}

	@Override
	public Collection<Annotation> getQualifiers() {
		return qualifiers.values();
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
	public boolean isOptional() {
		return false;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public Optional<String> getNamed() {
		return Optional.empty();
	}

}

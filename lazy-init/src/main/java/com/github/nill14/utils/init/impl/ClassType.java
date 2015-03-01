package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import javax.inject.Qualifier;

import com.github.nill14.utils.init.api.IType;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.google.common.collect.ImmutableMap;

public class ClassType implements IType {
	
	private final Class<?> clazz;
	private final ImmutableMap<Class<? extends Annotation>, Annotation> qualifiers;
	private final ImmutableMap<Class<? extends Annotation>, Annotation> annotations;

	public ClassType(Class<?> clazz) {
		this.clazz = clazz;
		qualifiers = ImmutableMap.copyOf(AnnotationScanner.findAnnotations(clazz, Qualifier.class));
		annotations = ImmutableMap.copyOf(AnnotationScanner.findAnnotations(clazz));
	}

	@Override
	public boolean isParametrized() {
		return false;
	}

	@Override
	public boolean isNamed() {
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
	public String getName() {
		return clazz.getName();
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

}

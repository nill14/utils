package com.github.nill14.utils.init.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import javax.inject.Named;
import javax.inject.Qualifier;

import com.github.nill14.utils.init.api.IType;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.google.common.collect.ImmutableMap;

public class FieldInjectionDescriptor implements IType {

	
	private final Field field;
	private final Optional<String> named;
	private final ImmutableMap<Class<?>, Annotation> qualifiers;
	private final ImmutableMap<Class<?>, Annotation> annotations;

	public FieldInjectionDescriptor(Field f) {
		field = f;
		Named named = f.getAnnotation(javax.inject.Named.class);
		this.named = Optional.ofNullable(named).map(n -> n.value());
		
		qualifiers = ImmutableMap.copyOf(AnnotationScanner.findAnnotations(f, Qualifier.class));
		annotations = ImmutableMap.copyOf(AnnotationScanner.findAnnotations(f));
	}
	
	public void inject(Object instance, Object value) {
		try {
			field.setAccessible(true);
			field.set(instance, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Optional<String> getNamed() {
		return named;
	}

	public Field getField() {
		return field;
	}
	
	@Override
	public boolean isParametrized() {
		Type genericType = field.getGenericType();
		return genericType instanceof ParameterizedType;
	}
	
	@Override
	public Class<?> getFirstParamClass() {
		Type genericType = field.getGenericType();
		if (genericType instanceof ParameterizedType) {
			Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
			if (type instanceof ParameterizedType) {
				return Class.class.cast(((ParameterizedType) type).getRawType());
			} else {
				return Class.class.cast(type);
			}
			
		}
		throw new IllegalStateException();
	}

	@Override
	public Type[] getParameterTypes() {
		Type genericType = field.getGenericType();
		if (genericType instanceof ParameterizedType) {
			return ((ParameterizedType) genericType).getActualTypeArguments();
		}
		throw new IllegalStateException();
	}
	
	@Override
	public Type getGenericType() {
		return field.getGenericType();
	}

	@Override
	public Class<?> getRawType() {
		return field.getType();
	}
	
	@Override
	public String getName() {
		return named.orElse(field.getName());
	}
	
	@Override
	public boolean isNamed() {
		return named.isPresent();
	}
	
	@Override
	public String toString() {
		return field.toString();
	}
	
	@Override
	public Collection<Annotation> getQualifiers() {
		return qualifiers.values();
	}

	@Override
	public Collection<Annotation> getAnnotations() {
		return annotations.values();
	}
	
	@Override
	public Optional<Annotation> getAnnotation(
			Class<? extends Annotation> annotation) {
		
		return Optional.ofNullable(annotations.get(annotation));
	}
}

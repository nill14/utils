package com.github.nill14.utils.init.inject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import javax.inject.Named;

import com.github.nill14.utils.init.api.IType;

public class FieldInjectionDescriptor implements IType {

	
	private final Field field;
	private final Optional<String> named;

	public FieldInjectionDescriptor(Field f, Named named) {
		field = f;
		this.named = Optional.ofNullable(named).map(n -> n.value());
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
			return Class.class.cast(type);
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
	public String toString() {
		return field.toString();
	}
}

package com.github.nill14.utils.init.inject;

import java.lang.reflect.Field;
import java.util.Optional;

import javax.inject.Named;

public class FieldInjectionDescriptor {

	
	private final Field field;
	private final boolean mandatory;
	private final Optional<String> named;

	public FieldInjectionDescriptor(Field f, boolean mandatory, Named named) {
		this.field = f;
		this.mandatory = mandatory;
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

	public boolean isMandatory() {
		return mandatory;
	}
	
	
	public Field getField() {
		return field;
	}

	public Class<?> getType() {
		return field.getType();
	}

	public String getName() {
		return named.orElse(field.getName());
	}
}

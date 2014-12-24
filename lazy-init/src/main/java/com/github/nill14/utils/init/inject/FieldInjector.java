package com.github.nill14.utils.init.inject;

import java.lang.reflect.Field;
import java.util.Optional;

public class FieldInjector {
	private final Field f;
	private final boolean mandatory;
	private final Optional<String> named;

	public FieldInjector(Field f, boolean mandatory, String named) {
		this.f = f;
		this.mandatory = mandatory;
		this.named = Optional.ofNullable(named);
	}
	
	public Optional<String> getNamed() {
		return named;
	}
	
	public Class<?> getType() {
		return f.getType();
	}
	
	public String getName() {
		return f.getName();
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public void inject(Object instance, Object value) {
		try {
			f.setAccessible(true);
			f.set(instance, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
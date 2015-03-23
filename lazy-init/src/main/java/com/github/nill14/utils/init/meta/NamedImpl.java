package com.github.nill14.utils.init.meta;

import static com.google.common.base.Preconditions.*;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.inject.Named;

class NamedImpl implements Named, Serializable {

	private final String value;

	public NamedImpl(String value) {
		this.value = checkNotNull(value, "name");
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public int hashCode() {
		// This is specified in java.lang.Annotation.
		return (127 * "value".hashCode()) ^ value.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Named)) {
			return false;
		}

		Named other = (Named) o;
		return value.equals(other.value());
	}

	@Override
	public String toString() {
		return String.format("@%s(value=%s)", Named.class.getName(), value);
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Named.class;
	}

	private static final long serialVersionUID = 0;
}

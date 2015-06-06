package com.github.nill14.utils.init.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;


/*package*/ final class CounterImpl implements Counter, Serializable {

	private final long value;

	public CounterImpl(long value) {
		this.value = value;
	}

	@Override
	public long value() {
		return value;
	}

	@Override
	public int hashCode() {
		// This is specified in java.lang.Annotation.
		return (127 * "value".hashCode()) ^ Long.valueOf(value).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Counter)) {
			return false;
		}

		Counter other = (Counter) o;
		return value == other.value();
	}

	@Override
	public String toString() {
		return String.format("@%s(value=%s)", Counter.class.getName(), value);
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Counter.class;
	}

	private static final long serialVersionUID = 0;
}

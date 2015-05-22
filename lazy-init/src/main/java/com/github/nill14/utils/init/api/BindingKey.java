package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.meta.Annotations;
import com.google.common.reflect.TypeToken;

public final class BindingKey<T> {


	public static <T> BindingKey<T> of(Class<T> clazz) {
		return new BindingKey<>(TypeToken.of(clazz), null);
	}
	
	public static <T> BindingKey<T> of(TypeToken<T> typeToken) {
		return new BindingKey<>(typeToken, null);
	}
	
	public static <T> BindingKey<T> of(Class<T> clazz, String named) {
		return new BindingKey<>(TypeToken.of(clazz), Annotations.named(named));
	}
	
	public static <T> BindingKey<T> of(TypeToken<T> typeToken, String named) {
		return new BindingKey<>(typeToken, Annotations.named(named));
	}
	
	public static <T> BindingKey<T> of(Class<T> clazz, Class<? extends Annotation> annotationType) {
		return new BindingKey<>(TypeToken.of(clazz), Annotations.annotation(annotationType));
	}
	
	public static <T> BindingKey<T> of(TypeToken<T> typeToken, Class<? extends Annotation> annotationType) {
		return new BindingKey<>(typeToken, Annotations.annotation(annotationType));
	}
	
	public static <T> BindingKey<T> of(Class<T> clazz, @Nullable Annotation qualifier) {
		return new BindingKey<>(TypeToken.of(clazz), qualifier);
	}
	
	public static <T> BindingKey<T> of(TypeToken<T> typeToken, @Nullable Annotation qualifier) {
		return new BindingKey<>(typeToken, qualifier);
	}
	
	private final TypeToken<T> typeToken;
	private final @Nullable Annotation qualifier;

	/*temporarily package otherwise private*/ BindingKey(TypeToken<T> typeToken, @Nullable Annotation qualifier) {
		this.typeToken = typeToken;
		this.qualifier = qualifier;
	}
	
	public Class<?> getRawType() {
		return typeToken.getRawType();
	}

	public Type getGenericType() {
		return typeToken.getType();
	}
	
	public TypeToken<T> getToken() {
		return typeToken;
	}

	public @Nullable Annotation getQualifier() {
		return qualifier;
	}
	
	public @Nullable Class<? extends Annotation> getQualifierType() {
		if (qualifier != null) {
			return qualifier.annotationType();
		}
		return null;
	}
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
		result = prime * result + ((typeToken == null) ? 0 : typeToken.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BindingKey other = (BindingKey) obj;
		if (qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!qualifier.equals(other.qualifier))
			return false;
		if (typeToken == null) {
			if (other.typeToken != null)
				return false;
		} else if (!typeToken.equals(other.typeToken))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (qualifier != null) {
			return String.format("@%s %s", qualifier.annotationType().getSimpleName(), typeToken.toString());
		}
		return typeToken.toString();
	}


	
	
}
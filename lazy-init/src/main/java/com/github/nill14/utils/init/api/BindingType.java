package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.meta.Annotations;
import com.google.common.reflect.TypeToken;

/**
 * 
 *
 * @deprecated Use BindingKey instead
 */
@Deprecated
public final class BindingType<T> extends BindingKey<T> {


	public static <T> BindingType<T> of(Class<T> clazz) {
		return new BindingType<>(TypeToken.of(clazz), null);
	}
	
	public static <T> BindingType<T> of(TypeToken<T> typeToken) {
		return new BindingType<>(typeToken, null);
	}
	
	public static <T> BindingType<T> of(Class<T> clazz, String named) {
		return new BindingType<>(TypeToken.of(clazz), Annotations.named(named));
	}
	
	public static <T> BindingType<T> of(TypeToken<T> typeToken, String named) {
		return new BindingType<>(typeToken, Annotations.named(named));
	}
	
	public static <T> BindingType<T> of(Class<T> clazz, Class<? extends Annotation> annotationType) {
		return new BindingType<>(TypeToken.of(clazz), Annotations.annotation(annotationType));
	}
	
	public static <T> BindingType<T> of(TypeToken<T> typeToken, Class<? extends Annotation> annotationType) {
		return new BindingType<>(typeToken, Annotations.annotation(annotationType));
	}
	
	public static <T> BindingType<T> of(Class<T> clazz, Annotation qualifier) {
		return new BindingType<>(TypeToken.of(clazz), qualifier);
	}
	
	public static <T> BindingType<T> of(TypeToken<T> typeToken, Annotation qualifier) {
		return new BindingType<>(typeToken, qualifier);
	}
	

	private BindingType(TypeToken<T> typeToken, @Nullable Annotation qualifier) {
		super(typeToken, qualifier);
	}


	
	
}
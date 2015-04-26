package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;

import com.google.common.reflect.TypeToken;
import com.google.inject.Scope;

public interface Binder {

	<T> AnnotatedBindingBuilder<T> bind(TypeToken<T> typeToken);

	<T> AnnotatedBindingBuilder<T> bind(Class<T> type);
	
	/**
	 * Binds a scope to a a scope annotation. 
	 * The scope annotation must be annotated with {@link Scope}
	 */
	void bindScope(Class<? extends Annotation> annotationType, Scope scope);

}

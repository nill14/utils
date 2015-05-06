package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;

import com.github.nill14.utils.annotation.Experimental;
import com.github.nill14.utils.init.api.IScope;
import com.google.common.reflect.TypeToken;

public interface Binder {

	<T> AnnotatedBindingBuilder<T> bind(TypeToken<T> typeToken);

	<T> AnnotatedBindingBuilder<T> bind(Class<T> type);
	
	/**
	 * Binds a scope to a a scope annotation. 
	 * The scope annotation must be annotated with {@link IScope}
	 */
	void bindScope(Class<? extends Annotation> annotationType, IScope scope);
	
	@Experimental
	@Deprecated
	IScope getScope(Class<? extends Annotation> annotationType);

}

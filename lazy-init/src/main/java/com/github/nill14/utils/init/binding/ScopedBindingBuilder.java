package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;

public interface ScopedBindingBuilder {

	void in(Class<? extends Annotation> scopeAnnotation);

	// void in(Scope scope);

}

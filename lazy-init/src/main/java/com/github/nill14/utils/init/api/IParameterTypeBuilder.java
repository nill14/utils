package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;

public interface IParameterTypeBuilder<T> {

	@Deprecated
	IParameterTypeBuilder<T> withAnnotation(Annotation annotation);

	IParameterTypeBuilder<T> annotatedWith(Class<? extends Annotation> annotationType);

	IParameterTypeBuilder<T> annotatedWith(Annotation annotation);

	@Deprecated
	IParameterTypeBuilder<T> withAnnotationType(Class<? extends Annotation> annotationType);

	IParameterTypeBuilder<T> scanAnnotations();

	IParameterTypeBuilder<T> scanQualifiers();

	IParameterTypeBuilder<T> named(String name);

	@Deprecated
	IParameterTypeBuilder<T> withName(String name);

	IParameterType<T> build();


}
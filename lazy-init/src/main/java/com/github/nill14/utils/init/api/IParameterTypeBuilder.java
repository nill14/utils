package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;

public interface IParameterTypeBuilder {

	@Deprecated
	IParameterTypeBuilder withAnnotation(Annotation annotation);

	IParameterTypeBuilder annotatedWith(Class<? extends Annotation> annotationType);

	IParameterTypeBuilder annotatedWith(Annotation annotation);

	@Deprecated
	IParameterTypeBuilder withAnnotationType(Class<? extends Annotation> annotationType);

	IParameterTypeBuilder scanAnnotations();

	IParameterTypeBuilder scanQualifiers();

	IParameterTypeBuilder named(String name);

	@Deprecated
	IParameterTypeBuilder withName(String name);

	IParameterType build();


}
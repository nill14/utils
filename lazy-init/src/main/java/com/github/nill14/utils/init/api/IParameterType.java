package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import com.github.nill14.utils.init.impl.ParameterTypeBuilder;
import com.google.common.reflect.TypeToken;

public interface IParameterType {

	Class<?> getRawType();

	Type getGenericType();
	
	boolean isParametrized();

	Type[] getParameterTypes();
	
	Class<?> getFirstParamClass();
	
	Collection<Annotation> getQualifiers();
	
	Optional<Annotation> getAnnotation(Class<? extends Annotation> annotation);
	
	Collection<Annotation> getAnnotations();

	boolean isOptional();
	
	boolean isNullable();

	Optional<String> getNamed();

	static IParameterType of(Class<?> clazz) {
		return ParameterTypeBuilder.builder(clazz).build();
	}

	static IParameterType of(TypeToken<?> typeToken) {
		return ParameterTypeBuilder.builder(typeToken).build();
	}
	//TODO consider boolean isCollection and TypeToken getDependencyType
	
}
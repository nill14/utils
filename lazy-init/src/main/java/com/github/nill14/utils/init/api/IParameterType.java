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
	
	TypeToken<?> getToken();

	boolean isParametrized();

	Type[] getParameterTypes();
	
	TypeToken<?> getFirstParamToken();
	
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
	
	static IParameterTypeBuilder builder(Class<?> clazz) {
		return ParameterTypeBuilder.builder(clazz);
	}
	
	static IParameterTypeBuilder builder(TypeToken<?> typeToken) {
		return ParameterTypeBuilder.builder(typeToken);
	}

	
	//TODO consider boolean isCollection and TypeToken getDependencyType
	
}
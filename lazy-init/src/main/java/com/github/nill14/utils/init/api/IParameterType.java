package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import com.github.nill14.utils.init.impl.ParameterTypeBuilder;
import com.google.common.reflect.TypeToken;

public interface IParameterType<T> {

	Class<?> getRawType();

	Type getGenericType();
	
	TypeToken<T> getToken();

	boolean isParametrized();

	Type[] getParameterTypes();
	
	TypeToken<?> getFirstParamToken();
	
	Collection<Annotation> getQualifiers();
	
	Optional<Annotation> getAnnotation(Class<? extends Annotation> annotation);
	
	Collection<Annotation> getAnnotations();

	boolean isOptional();
	
	boolean isNullable();

	Optional<String> getNamed();

	static <T> IParameterType<T> of(Class<T> clazz) {
		return ParameterTypeBuilder.builder(clazz).build();
	}

	static <T> IParameterType<T> of(TypeToken<T> typeToken) {
		return ParameterTypeBuilder.builder(typeToken).build();
	}
	
	static <T> IParameterTypeBuilder<T> builder(Class<T> clazz) {
		return ParameterTypeBuilder.builder(clazz);
	}
	
	static <T> IParameterTypeBuilder<T> builder(TypeToken<T> typeToken) {
		return ParameterTypeBuilder.builder(typeToken);
	}

	
	//TODO consider boolean isCollection and TypeToken getDependencyType
	
}
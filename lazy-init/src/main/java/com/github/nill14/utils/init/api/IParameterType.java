package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.inject.ParameterTypeInjectionDescriptor;
import com.google.common.reflect.TypeToken;

public interface IParameterType {

	Class<?> getRawType();

	Type getGenericType();
	
	TypeToken<?> getToken();
	
	BindingKey<?> getBindingKey();

	boolean isParametrized();

	@Nullable Class<?> getDeclaringClass();
	
	Type[] getParameterTypes();
	
	TypeToken<?> getFirstParamToken();
	
	IParameterType getFirstParamType();
	
	@Nullable Annotation getQualifier();
	
	Optional<Annotation> getAnnotation(Class<? extends Annotation> annotation);
	
	Collection<Annotation> getAnnotations();

	boolean isOptional();
	
	/**
	 * 
	 * @return if is subclass of Iterable
	 */
	boolean isCollection();
	
	boolean isNullable();

	Optional<String> getNamed();

	static <T> IParameterType of(Class<T> clazz) {
		return ParameterTypeInjectionDescriptor.of(BindingKey.of(clazz));
	}

	static <T> IParameterType of(TypeToken<T> typeToken) {
		return ParameterTypeInjectionDescriptor.of(BindingKey.of(typeToken)); 
	}


	static <T> IParameterType of(BindingKey<T> BindingKey) {
		return ParameterTypeInjectionDescriptor.of(BindingKey);
	}
	
	
	//TODO consider boolean isCollection and TypeToken getDependencyType
	
}
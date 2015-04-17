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

	boolean isParametrized();

	@Nullable Class<?> getDeclaringClass();
	
	Type[] getParameterTypes();
	
	TypeToken<?> getFirstParamToken();
	
	IParameterType getFirstParamType();
	
	@Nullable Annotation getQualifier();
	
	Optional<Annotation> getAnnotation(Class<? extends Annotation> annotation);
	
	Collection<Annotation> getAnnotations();

	boolean isOptional();
	
	boolean isNullable();

	Optional<String> getNamed();

	static <T> IParameterType of(Class<T> clazz) {
		return ParameterTypeInjectionDescriptor.of(BindingType.of(clazz));
	}

	static <T> IParameterType of(TypeToken<T> typeToken) {
		return ParameterTypeInjectionDescriptor.of(BindingType.of(typeToken)); 
	}


	static <T> IParameterType of(BindingType<T> bindingType) {
		return ParameterTypeInjectionDescriptor.of(bindingType);
	}
	
	
	//TODO consider boolean isCollection and TypeToken getDependencyType
	
}
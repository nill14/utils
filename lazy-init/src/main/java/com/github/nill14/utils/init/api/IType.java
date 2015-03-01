package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import com.github.nill14.utils.init.impl.ClassType;

public interface IType {

	boolean isParametrized();
	
	boolean isNamed();

	Type[] getParameterTypes();

	Class<?> getRawType();

	Type getGenericType();
	
	String getName();

	Class<?> getFirstParamClass();
	
	Collection<Annotation> getQualifiers();
	
	Optional<Annotation> getAnnotation(Class<? extends Annotation> annotation);
	
	Collection<Annotation> getAnnotations();
	
	
	
	public static IType fromClass(Class<?> clazz) {
		return new ClassType(clazz);
	}

}
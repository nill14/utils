package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

public interface IType {

	boolean isParametrized();
	
	boolean isNamed();

	Type[] getParameterTypes();

	Class<?> getRawType();

	String getName();

	Class<?> getFirstParamClass();
	
	Collection<Annotation> getQualifiers();
	
	Optional<Annotation> getQualifier(Class<? extends Annotation> annotation);

}
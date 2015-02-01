package com.github.nill14.utils.init.api;

import java.lang.reflect.Type;

public interface IType {

	boolean isParametrized();
	
	boolean isNamed();

	Type[] getParameterTypes();

	Class<?> getRawType();

	String getName();

	Class<?> getFirstParamClass();

}
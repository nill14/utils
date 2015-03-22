package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public interface IBeanDescriptor<T> {

	List<? extends IMemberDescriptor> getFieldDescriptors();

	List<? extends IMemberDescriptor> getMethodDescriptors();

	List<? extends IMemberDescriptor> getConstructorDescriptors();

	Set<Class<? super T>> getInterfaces();

	Set<Class<? super T>> getDeclaredTypes();

	@Deprecated
	Set<Annotation> getDeclaredQualifiers();

	Class<T> getRawType();
	
	Type getGenericType();

	boolean canBeInstantiated();

}
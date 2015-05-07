package com.github.nill14.utils.init.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;

import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class FieldInjectionDescriptor implements IMemberDescriptor {

	
	private final Field field;
	private final ImmutableMap<Class<?>, Annotation> annotations;
	private final ParameterTypeInjectionDescriptor parameterType;

	public FieldInjectionDescriptor(Field f, Class<?> declaringClass) {
		field = f;
		annotations = ImmutableMap.copyOf(AnnotationScanner.indexAnnotations(f));
		parameterType = ParameterTypeInjectionDescriptor.of(field.getGenericType(), field.getAnnotations(), f, declaringClass);
	}
	
	@Override
	public String toString() {
		return field.toGenericString();
	}
	
	@Override
	public Collection<Annotation> getAnnotations() {
		return annotations.values();
	}
	
	@Override
	public Optional<Annotation> getAnnotation(Class<? extends Annotation> annotation) {
		return Optional.ofNullable(annotations.get(annotation));
	}

	@Override
	public Collection<IParameterType> getParameterTypes() {
		return ImmutableList.of(parameterType);
	}
	
	public ParameterTypeInjectionDescriptor getParameterType() {
		return parameterType;
	}

	@Override
	public Object invoke(Object receiver, Object... args) throws InvocationTargetException, ReflectiveOperationException {
		Preconditions.checkArgument(args.length == 1, args);
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		field.set(receiver, args[0]);
		return null;
	}


	@Override
	public boolean isOptionalInject() {
		return parameterType.isNullable();
	}
 	
}

package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Optional;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IQualifiedProvider;
import com.google.common.reflect.TypeToken;

@SuppressWarnings("unchecked")
public class QualifiedProvider<T> implements IQualifiedProvider<T> {
	
	private final IPropertyResolver resolver;
	private final TypeToken<T> typeToken;

	public QualifiedProvider(TypeToken<T> typeToken, IPropertyResolver resolver) {
		this.typeToken = typeToken;
		this.resolver = resolver;
	}
	

	private T resolveOrThrow(IParameterType type) {
		T object = (T) resolver.resolve(null, type);
		if (object != null) {
			return object;
		} else {
			throw new RuntimeException("Cannot resolve " + type);
		}
	}
	
	private Optional<T> resolveOptional(IParameterType type) {
		return Optional.ofNullable((T) resolver.resolve(null, type));
	}

	@Override
	public T getNamed(String named) {
		IParameterType type = ParameterTypeBuilder.builder(typeToken).withName(named).build();
		return resolveOrThrow(type);
	}

	@Override
	public Optional<T> getOptionalNamed(String named) {
		IParameterType type = ParameterTypeBuilder.builder(typeToken).withName(named).build();
		return resolveOptional(type);
	}

	@Override
	public T getQualified(Class<? extends Annotation> annotationType) {
		IParameterType type = ParameterTypeBuilder.builder(typeToken).withAnnotationType(annotationType).build();
		return resolveOrThrow(type);
	}

	@Override
	public Optional<T> getOptionalQualified(Class<? extends Annotation> annotationType) {
		IParameterType type = ParameterTypeBuilder.builder(typeToken).withAnnotationType(annotationType).build();
		return resolveOptional(type);
	}

	@Override
	public T getQualified(Annotation annotation) {
		IParameterType type = ParameterTypeBuilder.builder(typeToken).withAnnotation(annotation).build();
		return resolveOrThrow(type);
	}

	@Override
	public Optional<T> getOptionalQualified(Annotation annotation) {
		IParameterType type = ParameterTypeBuilder.builder(typeToken).withAnnotation(annotation).build();
		return resolveOptional(type);
	}
}

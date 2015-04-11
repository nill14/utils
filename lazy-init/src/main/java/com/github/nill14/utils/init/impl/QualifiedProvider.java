package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.inject.Provider;

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
	

	private T resolveOrThrow(IParameterType<?> type) {
		T object = (T) resolver.resolve(null, type);
		if (object != null) {
			return object;
		} else {
			throw new RuntimeException("Cannot resolve " + type.getQualifiers());
		}
	}
	
	private Optional<T> resolveOptional(IParameterType<?> type) {
		return Optional.ofNullable((T) resolver.resolve(null, type));
	}

	@Override
	public T getNamed(String named) {
		IParameterType<?> type = IParameterType.builder(typeToken).named(named).build();
		return resolveOrThrow(type);
	}

	@Override
	public Optional<T> getOptionalNamed(String named) {
		IParameterType<?> type = IParameterType.builder(typeToken).named(named).build();
		return resolveOptional(type);
	}

	@Override
	public T getQualified(Class<? extends Annotation> annotationType) {
		IParameterType<?> type = IParameterType.builder(typeToken).annotatedWith(annotationType).build();
		return resolveOrThrow(type);
	}

	@Override
	public Optional<T> getOptionalQualified(Class<? extends Annotation> annotationType) {
		IParameterType<?> type = IParameterType.builder(typeToken).annotatedWith(annotationType).build();
		return resolveOptional(type);
	}

	@Override
	public T getQualified(Annotation annotation) {
		IParameterType<?> type = IParameterType.builder(typeToken).annotatedWith(annotation).build();
		return resolveOrThrow(type);
	}

	@Override
	public Optional<T> getOptionalQualified(Annotation annotation) {
		IParameterType<?> type = IParameterType.builder(typeToken).annotatedWith(annotation).build();
		return resolveOptional(type);
	}
	
	private final Provider<QualifiedProvider<T>> provider = new Provider<QualifiedProvider<T>>() {
		
		@Override
		public QualifiedProvider<T> get() {
			return QualifiedProvider.this;
		}
	};
	
	public Provider<QualifiedProvider<T>> toProvider() {
		return provider;
	}
}

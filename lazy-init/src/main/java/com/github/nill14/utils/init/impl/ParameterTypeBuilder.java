package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Named;
import javax.inject.Qualifier;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IParameterTypeBuilder;
import com.github.nill14.utils.init.inject.ParameterTypeInjectionDescriptor;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.github.nill14.utils.init.meta.Annotations;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

public class ParameterTypeBuilder<T> implements IParameterTypeBuilder<T> {

//	static {
		//see https://code.google.com/p/guava-libraries/wiki/ReflectionExplained
//		TypeToken.of(method.getGenericReturnType()).
//		TypeToken<List<String>> stringListTok = new TypeToken<List<String>>() {};
//		TypeToken<Map<?, ?>> wildMapTok = new TypeToken<Map<?, ?>>() {};
//		builder(new TypeToken<Optional<List<String>>>() {});
//	}
	
	public static <T> IParameterTypeBuilder<T> builder(Class<T> clazz) {
		return new ParameterTypeBuilder<>(clazz);
	}
	
	public static <T> IParameterTypeBuilder<T> builder(TypeToken<T> typeToken) {
		return new ParameterTypeBuilder<>(typeToken);
	}
	
	private final TypeToken<?> typeToken;
	private final ImmutableMap.Builder<Class<? extends Annotation>, Annotation> annotations = ImmutableMap.builder();

	private ParameterTypeBuilder(Class<T> clazz) {
		typeToken = TypeToken.of(clazz);
	}
	
	private ParameterTypeBuilder(TypeToken<T> typeToken) {
		this.typeToken = typeToken;
	}
	
	@Override
	@Deprecated
	public ParameterTypeBuilder<T> withAnnotation(Annotation annotation) {
		annotations.put(annotation.annotationType(), annotation);
		return this;
	}
	
	@Override
	public ParameterTypeBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
		annotations.put(annotationType, Annotations.annotation(annotationType));
		return this;
	}

	@Override
	public ParameterTypeBuilder<T> annotatedWith(Annotation annotation) {
		annotations.put(annotation.annotationType(), annotation);
		return this;
	}
	
	@Override
	@Deprecated
	public ParameterTypeBuilder<T> withAnnotationType(Class<? extends Annotation> annotationType) {
		annotations.put(annotationType, Annotations.annotation(annotationType));
		return this;
	}

	@Override
	public ParameterTypeBuilder<T> scanAnnotations() {
		Annotation[] annotations = typeToken.getRawType().getAnnotations();
		this.annotations.putAll(AnnotationScanner.indexAnnotations(annotations));
		return this;
	}

	@Override
	public ParameterTypeBuilder<T> scanQualifiers() {
		Annotation[] annotations = typeToken.getRawType().getAnnotations();
		this.annotations.putAll(AnnotationScanner.findAnnotations(annotations, Qualifier.class));
		return this;
	}
	
	@Override
	public ParameterTypeBuilder<T> named(String name) {
		annotations.put(Named.class, Annotations.named(name));
		return this;
	}
	
	@Override
	@Deprecated
	public ParameterTypeBuilder<T> withName(String name) {
		annotations.put(Named.class, Annotations.named(name));
		return this;
	}
	
	@Override
	public IParameterType<T> build() {
		Annotation[] annotations = this.annotations.build().values().stream().toArray(Annotation[]::new);
		Type type = typeToken.getType();
		return new ParameterTypeInjectionDescriptor<>(type, annotations);
	}

}

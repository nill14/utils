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

public class ParameterTypeBuilder implements IParameterTypeBuilder {

//	static {
		//see https://code.google.com/p/guava-libraries/wiki/ReflectionExplained
//		TypeToken.of(method.getGenericReturnType()).
//		TypeToken<List<String>> stringListTok = new TypeToken<List<String>>() {};
//		TypeToken<Map<?, ?>> wildMapTok = new TypeToken<Map<?, ?>>() {};
//		builder(new TypeToken<Optional<List<String>>>() {});
//	}
	
	public static <T> IParameterTypeBuilder builder(Class<T> clazz) {
		return new ParameterTypeBuilder(clazz);
	}
	
	public static <T> IParameterTypeBuilder builder(TypeToken<T> typeToken) {
		return new ParameterTypeBuilder(typeToken);
	}
	
	private final TypeToken<?> typeToken;
	private final ImmutableMap.Builder<Class<? extends Annotation>, Annotation> annotations = ImmutableMap.builder();

	private ParameterTypeBuilder(Class<?> clazz) {
		typeToken = TypeToken.of(clazz);
	}
	
	private ParameterTypeBuilder(TypeToken<?> typeToken) {
		this.typeToken = typeToken;
	}
	
	@Override
	@Deprecated
	public ParameterTypeBuilder withAnnotation(Annotation annotation) {
		annotations.put(annotation.annotationType(), annotation);
		return this;
	}
	
	@Override
	public ParameterTypeBuilder annotatedWith(Class<? extends Annotation> annotationType) {
		annotations.put(annotationType, Annotations.annotation(annotationType));
		return this;
	}

	@Override
	public ParameterTypeBuilder annotatedWith(Annotation annotation) {
		annotations.put(annotation.annotationType(), annotation);
		return this;
	}
	
	@Override
	@Deprecated
	public ParameterTypeBuilder withAnnotationType(Class<? extends Annotation> annotationType) {
		annotations.put(annotationType, Annotations.annotation(annotationType));
		return this;
	}

	@Override
	public ParameterTypeBuilder scanAnnotations() {
		Annotation[] annotations = typeToken.getRawType().getAnnotations();
		this.annotations.putAll(AnnotationScanner.indexAnnotations(annotations));
		return this;
	}

	@Override
	public ParameterTypeBuilder scanQualifiers() {
		Annotation[] annotations = typeToken.getRawType().getAnnotations();
		this.annotations.putAll(AnnotationScanner.findAnnotations(annotations, Qualifier.class));
		return this;
	}
	
	@Override
	public ParameterTypeBuilder named(String name) {
		annotations.put(Named.class, Annotations.named(name));
		return this;
	}
	
	@Override
	@Deprecated
	public ParameterTypeBuilder withName(String name) {
		annotations.put(Named.class, Annotations.named(name));
		return this;
	}
	
	@Override
	public IParameterType build() {
		Annotation[] annotations = this.annotations.build().values().stream().toArray(Annotation[]::new);
		Type type = typeToken.getType();
		return new ParameterTypeInjectionDescriptor(type, annotations);
	}

}

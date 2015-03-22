package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.inject.ParameterTypeInjectionDescriptor;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.inject.name.Names;

@SuppressWarnings("serial")
public class ParameterTypeBuilder {

//	static {
		//see https://code.google.com/p/guava-libraries/wiki/ReflectionExplained
//		TypeToken.of(method.getGenericReturnType()).
//		TypeToken<List<String>> stringListTok = new TypeToken<List<String>>() {};
//		TypeToken<Map<?, ?>> wildMapTok = new TypeToken<Map<?, ?>>() {};
//		builder(new TypeToken<Optional<List<String>>>() {});
//	}
	
	public static <T> ParameterTypeBuilder builder(Class<T> clazz) {
		return new ParameterTypeBuilder(clazz);
	}
	
	public static <T> ParameterTypeBuilder builder(TypeToken<T> typeToken) {
		return new ParameterTypeBuilder(typeToken);
	}
	
	private final TypeToken<?> typeToken;
	private final List<Annotation> annotations = Lists.newArrayList();

	private ParameterTypeBuilder(Class<?> clazz) {
		typeToken = TypeToken.of(clazz);
	}
	
	private ParameterTypeBuilder(TypeToken<?> typeToken) {
		this.typeToken = typeToken;
	}
	
	public ParameterTypeBuilder withAnnotation(Annotation annotation) {
		annotations.add(annotation);
		return this;
	}

	public ParameterTypeBuilder scanAnnotations() {
		annotations.addAll(Arrays.asList(typeToken.getRawType().getAnnotations()));
		return this;
	}
	
	public ParameterTypeBuilder withName(String name) {
		annotations.add(Names.named(name));
		return this;
	}
	
	public IParameterType build() {
		Annotation[] annotations = this.annotations.stream().toArray(Annotation[]::new);
		Type type = typeToken.getType();
		return new ParameterTypeInjectionDescriptor(type, annotations);
	}

}

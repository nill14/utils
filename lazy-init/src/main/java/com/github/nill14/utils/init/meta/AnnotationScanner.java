package com.github.nill14.utils.init.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Qualifier;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class AnnotationScanner {
	
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> indexAnnotations(Annotation[] annotations) {
		return indexAnnotations(Stream.of(annotations));	
	}	
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> indexAnnotations(Stream<Annotation> annotations) {
		return Maps.uniqueIndex(annotations.iterator(), a -> a.annotationType());	
	}	
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> findAnnotations(Annotation[] annotations, Class<? extends Annotation> metaAnnotation) {
		return findAnnotations(Stream.of(annotations), metaAnnotation);
	}
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> findAnnotations(Stream<Annotation> annotations, Class<? extends Annotation> metaAnnotation) {
		return indexAnnotations(annotations.filter(a -> a.annotationType().isAnnotationPresent(metaAnnotation)));
	}
	
	public static Optional<Annotation> findQualifier(Annotation[] annotations, GenericDeclaration classOrMethod) {
		return findQualifier(Stream.of(annotations), classOrMethod);
	}
	
	public static Optional<Annotation> findQualifier(Stream<Annotation> annotations, GenericDeclaration classOrMethod) {
		ImmutableCollection<Annotation> values = findAnnotations(annotations, Qualifier.class).values();
		if (values.size() > 1) {
			throw new IllegalArgumentException(String.format("%s can have at most one qualifier", classOrMethod));
		}
		else if (values.isEmpty()) {
			return Optional.empty();
		}
		else {
			return Optional.of(values.iterator().next());
		}
	}
	
	public static Optional<Annotation> findScope(Annotation[] annotations, GenericDeclaration classOrMethod) {
		return findScope(Stream.of(annotations), classOrMethod);
	}
	
	public static Optional<Annotation> findScope(Stream<Annotation> annotations, GenericDeclaration classOrMethod) {
		ImmutableCollection<Annotation> values = findAnnotations(annotations, Qualifier.class).values();
		if (values.size() > 1) {
			throw new IllegalArgumentException(String.format("%s can have at most one scope annotation", classOrMethod));
		}
		else if (values.isEmpty()) {
			return Optional.empty();
		}
		else {
			return Optional.of(values.iterator().next());
		}
	}	
	

	
}

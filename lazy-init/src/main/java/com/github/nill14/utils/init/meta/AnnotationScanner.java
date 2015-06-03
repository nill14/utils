package com.github.nill14.utils.init.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.inject.Qualifier;
import javax.inject.Scope;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class AnnotationScanner {
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> indexAnnotations(AnnotatedElement member) {
		return indexAnnotations(Stream.of(member.getAnnotations()));	
	}	
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> indexAnnotations(Annotation[] annotations) {
		return indexAnnotations(Stream.of(annotations));	
	}	
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> indexAnnotations(Stream<Annotation> annotations) {
		return Maps.uniqueIndex(annotations.iterator(), a -> a.annotationType());	
	}	
	
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> findAnnotations(AnnotatedElement member, Class<? extends Annotation> metaAnnotation) {
		return findAnnotations(Stream.of(member.getAnnotations()), metaAnnotation);
	}
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> findAnnotations(Annotation[] annotations, Class<? extends Annotation> metaAnnotation) {
		return findAnnotations(Stream.of(annotations), metaAnnotation);
	}
	
	public static ImmutableMap<Class<? extends Annotation>, Annotation> findAnnotations(Stream<Annotation> annotations, Class<? extends Annotation> metaAnnotation) {
		return indexAnnotations(annotations.filter(a -> a.annotationType().isAnnotationPresent(metaAnnotation)));
	}
	
	public static Optional<Annotation> findQualifier(@Nullable AnnotatedElement member) {
		if (member != null) {
			return findQualifier(Stream.of(member.getAnnotations()), member);
		
		} else {
			return Optional.empty();
		}
	}
	
	public static Optional<Annotation> findQualifier(Stream<Annotation> annotations, AnnotatedElement member) {
		ImmutableCollection<Annotation> values = findAnnotations(annotations, Qualifier.class).values();
		if (values.size() > 1) {
			throw new IllegalArgumentException(String.format("%s can have at most one qualifier", member));
		}
		else if (values.isEmpty()) {
			return Optional.empty();
		}
		else {
			return Optional.of(values.iterator().next());
		}
	}
	
	public static Optional<Annotation> findScope(AnnotatedElement member) {
		return findScope(Stream.of(member.getAnnotations()), member);
	}
	
	public static Optional<Annotation> findScope(Stream<Annotation> annotations, AnnotatedElement member) {
		ImmutableCollection<Annotation> values = findAnnotations(annotations, Scope.class).values();
		if (values.size() > 1) {
			throw new IllegalArgumentException(String.format("%s can have at most one scope annotation", member));
		}
		else if (values.isEmpty()) {
			return Optional.empty();
		}
		else {
			return Optional.of(values.iterator().next());
		}
	}	
	

	
}

package com.github.nill14.utils.init.meta;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

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
	

	
}

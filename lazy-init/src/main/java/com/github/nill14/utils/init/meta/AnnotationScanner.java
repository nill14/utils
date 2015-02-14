package com.github.nill14.utils.init.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationScanner {
	
	
	public static Map<Class<? extends Annotation>, Annotation> findAnnotations(Field field, Class<? extends Annotation> metaAnnotation) {
		return Stream.of(field.getAnnotations())
			.filter(a -> a.annotationType().isAnnotationPresent(metaAnnotation))
			.collect(Collectors.toMap(a -> a.annotationType(), a -> a));	
	}

	
}

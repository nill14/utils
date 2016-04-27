/**
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.nill14.utils.init.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.inject.Named;

import com.github.nill14.utils.init.meta.impl.AnnotationInvocationHandler;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Reflection;


public class Annotations {

	private Annotations() {}

	public static Named named(String name) {
		return new NamedImpl(name);
	}

	/**
	 * Generates an annotation of the expected annotation type. <br>
	 * All annotation type properties are expected to have a default value.
	 * 
	 * @param annotationType The annotation class
	 * @return annotation object for the annotation class
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T annotation(Class<T> annotationType) {
		Preconditions.checkState(isAllDefaultMethods(annotationType),
				"%s is not all default methods", annotationType);
		return (T) cache0.getUnchecked(annotationType);
	}
	
	
	/**
	 * Generates an annotation of the expected annotation type. 
	 * 
	 * @param annotationType The annotation class
	 * @param value The result of the annotation's value() method
	 * @return annotation object for the annotation class
	 */
	public static <T extends Annotation> T withValue(Class<T> annotationType, String value) {
		return withValue0(annotationType, value);
	}
	
	/**
	 * Generates an annotation of the expected annotation type. 
	 * 
	 * @param annotationType The annotation class
	 * @param value The result of the annotation's value() method
	 * @return annotation object for the annotation class
	 * 
	 */
	public static <T extends Annotation> T withValue(Class<T> annotationType, int value) {
		return withValue0(annotationType, value);
	}	
	
	/**
	 * Generates an annotation of the expected annotation type. 
	 * 
	 * @param annotationType The annotation class
	 * @param value The result of the annotation's value() method
	 * @return annotation object for the annotation class
	 */
	public static <T extends Annotation> T withValue(Class<T> annotationType, Class<?> value) {
		return withValue0(annotationType, value);
	}	
	
	public static <T extends Annotation, E extends Enum<E>> T withValue(Class<T> annotationType, E value) {
		return withValue0(annotationType, value);
	}
	
	/**
	 * Generates an annotation of the expected annotation type. 
	 * 
	 * @param annotationType The annotation class
	 * @param value The result of the annotation's value() method
	 * @return annotation object for the annotation class
	 */
	public static <T extends Annotation> T withValue(Class<T> annotationType, float value) {
		return withValue0(annotationType, value);
	}	
	
	/**
	 * Generates an annotation of the expected annotation type. 
	 * 
	 * @param annotationType The annotation class
	 * @param value The result of the annotation's value() method
	 * @return annotation object for the annotation class
	 */
	public static <T extends Annotation> T withValue(Class<T> annotationType, double value) {
		return withValue0(annotationType, value);
	}	
	
	/**
	 * Generates an annotation of the expected annotation type. 
	 * 
	 * @param annotationType The annotation class
	 * @param value The result of the annotation's value() method
	 * @return annotation object for the annotation class
	 */
	public static <T extends Annotation> T withValue(Class<T> annotationType, long value) {
		return withValue0(annotationType, value);
	}		
	
	
	/**
	 * Generates an Annotation with value for the annotation class. 
	 * All other values are defaults.
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Annotation> T withValue0(Class<T> annotationType, Object value) {
		Preconditions.checkState(isAllDefaultMethodsExceptValue(annotationType),
				"%s is not all default methods", annotationType);
		
		return (T) cache1.getUnchecked(new KeyWithValues<>(annotationType, definedValue(value)));
	}		
	
	private static ImmutableMap<String, Object> definedValue(Object value) {
		return ImmutableMap.of("value", value);
	}
	
	private static boolean isAllDefaultMethodsExceptValue(Class<? extends Annotation> annotationType) {
		for (Method m : annotationType.getDeclaredMethods()) {
			if (m.getDefaultValue() == null && !"value".equals(m.getName())) {
				return false;
			}
		}
		return true;
	}
  
	private static boolean isAllDefaultMethods(Class<? extends Annotation> annotationType) {
		for (Method m : annotationType.getDeclaredMethods()) {
			if (m.getDefaultValue() == null) {
				return false;
			}
		}
		return true;
	}
  
	private static final LoadingCache<Class<? extends Annotation>, Annotation> cache0 = CacheBuilder.newBuilder()
			.weakKeys()
			.build(new CacheLoader<Class<? extends Annotation>, Annotation>() {
				@Override
				public Annotation load(Class<? extends Annotation> annotationType) {
					return Reflection.newProxy(annotationType, new AnnotationInvocationHandler<>(annotationType));
				}
			});
	
	private static final LoadingCache<KeyWithValues<? extends Annotation>, Annotation> cache1 = CacheBuilder.newBuilder()
			.weakKeys()
			.build(new CacheLoader<KeyWithValues<? extends Annotation>, Annotation>() {
				@Override
				public Annotation load(KeyWithValues<? extends Annotation> key) {
					return Reflection.newProxy(key.annotationType, 
							new AnnotationInvocationHandler<>(key.annotationType, key.values));
				}
			});	


	private static final class KeyWithValues<T extends Annotation> {
		final Class<T> annotationType;
		final ImmutableMap<String, Object> values;

		public KeyWithValues(Class<T> annotationType, ImmutableMap<String, Object> values) {
			this.annotationType = annotationType;
			this.values = values;
		}
	}

}

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

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.Reflection;


public class Annotations {

	private Annotations() {}

	public static Named named(String name) {
		return new NamedImpl(name);
	}

	/**
	 * Generates an Annotation for the annotation class. 
	 * All values are defaults.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T annotation(Class<T> annotationType) {
		Preconditions.checkState(isAllDefaultMethods(annotationType),
				"%s is not all default methods", annotationType);
		return (T) cache.getUnchecked(annotationType);
	}
  
	private static boolean isAllDefaultMethods(Class<? extends Annotation> annotationType) {
		for (Method m : annotationType.getDeclaredMethods()) {
			if (m.getDefaultValue() == null) {
				return false;
			}
		}
		return true;
	}
  
	private static final LoadingCache<Class<? extends Annotation>, Annotation> cache = CacheBuilder.newBuilder()
			.weakKeys()
			.build(new CacheLoader<Class<? extends Annotation>, Annotation>() {
				@Override
				public Annotation load(Class<? extends Annotation> annotationType) {
					return Reflection.newProxy(annotationType,new AnnotationInvocationHandler<>(annotationType));
				}
			});
  

}

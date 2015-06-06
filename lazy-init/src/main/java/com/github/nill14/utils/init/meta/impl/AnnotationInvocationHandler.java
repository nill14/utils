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

package com.github.nill14.utils.init.meta.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class AnnotationInvocationHandler<T extends Annotation> implements
		InvocationHandler {

	private final Class<T> annotationType;
	private final ImmutableMap<String, Object> members;
	

	public AnnotationInvocationHandler(Class<T> annotationType) {
		this.annotationType = annotationType;
		members = resolveMembers(annotationType, Collections.emptyMap());
	}


	public AnnotationInvocationHandler(Class<T> annotationType, Map<String, Object> definedValues) {
		this.annotationType = annotationType;
		members = resolveMembers(annotationType, definedValues);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		if (annotationTypeMethod.equals(method)) {
			return annotationType;

		} else if (hashCodeMethod.equals(method)) {
			return annotationHashCode(annotationType, members);

		} else if (equalsMethod.equals(method)) {
			return annotationEquals(annotationType, members, args[0]);

		} else if (toStringMethod.equals(method)) {
			return annotationToString(annotationType, members);

		} else {
			return members.get(method.getName());
		}
	}

	private static ImmutableMap<String, Object> resolveMembers(
			Class<? extends Annotation> annotationType, Map<String, Object> definedValues) {
		ImmutableMap.Builder<String, Object> result = ImmutableMap.builder();
		for (Method method : annotationType.getDeclaredMethods()) {
			Object value = definedValues.getOrDefault(method.getName(), method.getDefaultValue());
			result.put(method.getName(), value);
		}
		return result.build();
	}

	/** Implements {@link Annotation#hashCode}. */
	private static int annotationHashCode(Class<? extends Annotation> type,
			Map<String, Object> members) throws Exception {
		int result = 0;
		for (Method method : type.getDeclaredMethods()) {
			String name = method.getName();
			Object value = members.get(name);
			result += (127 * name.hashCode())
					^ (Arrays.deepHashCode(new Object[] { value }) - 31);
		}
		return result;
	}

	/** Implements {@link Annotation#equals}. */
	private static boolean annotationEquals(Class<? extends Annotation> type,
			Map<String, Object> members, Object other) throws Exception {
		if (!type.isInstance(other)) {
			return false;
		}
		for (Method method : type.getDeclaredMethods()) {
			String name = method.getName();
			if (!Arrays.deepEquals(new Object[] { method.invoke(other) },
					new Object[] { members.get(name) })) {
				return false;
			}
		}
		return true;
	}

	private static final MapJoiner JOINER = Joiner.on(", ").withKeyValueSeparator("=");

	private static final Function<Object, String> DEEP_TO_STRING_FN = new Function<Object, String>() {
		@Override
		public String apply(Object arg) {
			String s = Arrays.deepToString(new Object[] { arg });
			return s.substring(1, s.length() - 1); // cut off brackets
		}
	};

	/** Implements {@link Annotation#toString}. */
	private static String annotationToString(Class<? extends Annotation> type,
			Map<String, Object> members) throws Exception {
		StringBuilder sb = new StringBuilder().append("@").append(type.getName()).append("(");
		JOINER.appendTo(sb, Maps.transformValues(members, DEEP_TO_STRING_FN));
		return sb.append(")").toString();
	}

	private static Method getMethod(Class<?> type, String name,
			Class<?>... argTypes) {
		try {
			return type.getDeclaredMethod(name, argTypes);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static final Method equalsMethod = getMethod(Object.class, "equals", Object.class);
	private static final Method hashCodeMethod = getMethod(Object.class, "hashCode");
	private static final Method toStringMethod = getMethod(Object.class, "toString");
	private static final Method annotationTypeMethod = getMethod(Annotation.class, "annotationType");
}

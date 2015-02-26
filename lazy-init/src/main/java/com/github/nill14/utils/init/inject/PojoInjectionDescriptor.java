package com.github.nill14.utils.init.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Qualifier;

import com.github.nill14.utils.init.api.IType;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.github.nill14.utils.java8.stream.GuavaCollectors;
import com.github.nill14.utils.java8.stream.StreamUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class PojoInjectionDescriptor {

	
	private final Class<?> clazz;
	private final ImmutableList<FieldInjectionDescriptor> properties;

	public PojoInjectionDescriptor(Class<?> pojoClazz) {
		clazz = pojoClazz;
		properties = properties(pojoClazz);
	}

	public ImmutableList<FieldInjectionDescriptor> getFieldDescriptors() {
		return properties;
	}
	
	public Set<Class<?>> getInterfaces() {
		return declaredClasses(clazz)
				.flatMap(cls -> Stream.of(cls.getInterfaces()))
				.collect(GuavaCollectors.toImmutableSet());
	}
	
	public Set<Class<?>> getDeclaredTypes() {
		return Stream.concat(declaredClasses(clazz), getInterfaces().stream())
				.collect(GuavaCollectors.toImmutableSet());
	}
	
	private Stream<Class<?>> declaredClasses(Class<?> clazz) {
		return StreamUtils.stream(new Iterator<Class<?>>() {
			private Class<?> c = clazz;
			
			@Override
			public boolean hasNext() {
				return c != null;
			}

			@Override
			public Class<?> next() {
				Class<?> result = c;
				c = c.getSuperclass();
				return result;
			}
		});
	}
	
	public Set<Annotation> getDeclaredQualifiers() {
		return ImmutableSet.copyOf(AnnotationScanner.findAnnotations(clazz, Qualifier.class).values());
	}
	
	private Stream<Field> nonStaticFields(Stream<Class<?>> declaredClasses) {
		return declaredClasses
			.flatMap(c -> Stream.of(c.getDeclaredFields()))
			.filter(f -> !Modifier.isStatic(f.getModifiers()));
	}

	private Stream<? extends FieldInjectionDescriptor> injectableFields(Stream<Field> nonStaticFields) {
		return nonStaticFields.map(f -> {
			if (f.isAnnotationPresent(javax.inject.Inject.class)) {
				
				return new FieldInjectionDescriptor(f);
			} 
			//else if ... com.google.inject.Inject from Guice have the optional parameter
			else return null;
		}).filter(x -> x != null);
	}	
	
	private ImmutableList<FieldInjectionDescriptor> properties(Class<?> pojoClazz) {
		return injectableFields(nonStaticFields(declaredClasses(pojoClazz)))
				.collect(GuavaCollectors.<FieldInjectionDescriptor>toImmutableList());
	}
	
	public List<Class<?>> getMandatoryDependencies() {
		return ImmutableList.copyOf(properties.stream()
				.map(PojoInjectionDescriptor::transformDependency).iterator());
	}

	public List<Class<?>> getOptionalDependencies() {
		return ImmutableList.copyOf(properties.stream()
				.map(PojoInjectionDescriptor::transformDependency).iterator());
	}
	
	private static Class<?> transformDependency(IType type) {
		Class<?> rawType = type.getRawType();
		if (type.isParametrized()) {
			if (Optional.class.equals(rawType)) {
				return type.getFirstParamClass();
			} else if (Collection.class.isAssignableFrom(rawType)) {
				return type.getFirstParamClass();
			} 
		} else if (rawType.isArray()) {
			return rawType.getComponentType();
		}
		
		return rawType;
	}
	
	public Class<?> getType() {
		return clazz;
	}
	
	
}

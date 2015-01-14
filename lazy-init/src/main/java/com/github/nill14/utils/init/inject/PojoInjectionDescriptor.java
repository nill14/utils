package com.github.nill14.utils.init.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Named;

import com.github.nill14.utils.java8.stream.GuavaCollectors;
import com.github.nill14.utils.java8.stream.StreamUtils;
import com.google.common.collect.ImmutableList;

public class PojoInjectionDescriptor {

	
	private final Class<?> clazz;
	private final ImmutableList<FieldInjectionDescriptor> properties;

	public PojoInjectionDescriptor(Class<?> pojoClazz) {
		this.clazz = pojoClazz;
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
	
	private Stream<Field> nonStaticFields(Stream<Class<?>> declaredClasses) {
		return declaredClasses
			.flatMap(c -> Stream.of(c.getDeclaredFields()))
			.filter(f -> !Modifier.isStatic(f.getModifiers()));
	}

	private Stream<? extends FieldInjectionDescriptor> injectableFields(Stream<Field> nonStaticFields) {
		return nonStaticFields.map(f -> {
			if (f.isAnnotationPresent(javax.inject.Inject.class)) {
				boolean mandatory = !f.isAnnotationPresent(javax.annotation.Nullable.class);
				Named named = f.getAnnotation(javax.inject.Named.class);
				return new FieldInjectionDescriptor(f, mandatory, named);
			} 
			//else if ... com.google.inject.Inject from Guice have the optional parameter
			else return null;
		}).filter(x -> x != null);
	}	
	
	private ImmutableList<FieldInjectionDescriptor> properties(Class<?> pojoClazz) {
		return injectableFields(nonStaticFields(declaredClasses(pojoClazz)))
				.collect(GuavaCollectors.toImmutableList());
	}
	
	public List<Class<?>> getMandatoryDependencies() {
		return ImmutableList.copyOf(properties.stream()
				.filter(p -> p.isMandatory())
				.map(p -> p.getType()).iterator());
	}

	public List<Class<?>> getOptionalDependencies() {
		return ImmutableList.copyOf(properties.stream()
				.filter(p -> !p.isMandatory())
				.map(p -> p.getType()).iterator());
	}
	
	public Class<?> getType() {
		return clazz;
	}
	
	
}

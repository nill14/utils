package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;

import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.FieldInjector;
import com.github.nill14.utils.java8.stream.StreamUtils;
import com.google.common.collect.ImmutableList;

public class AnnotationPojoInitializer implements IPojoInitializer<Object> {

	private static final long serialVersionUID = -6999206469201978450L;

	public static IPojoInitializer<Object> withResolver(IPropertyResolver resolver) {
		return new AnnotationPojoInitializer(resolver);
	}
	
	private final IPropertyResolver resolver;

	private AnnotationPojoInitializer(IPropertyResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public void init(Object instance) {
		doInject(instance);
		
		doPostConstruct(instance);
	}

	@Override
	public void destroy(Object instance) {
		doPreDestroy(instance);
		
	}
	
	public List<Class<?>> getMandatoryDependencies(Class<?> clazz) {
		return ImmutableList.copyOf(injectableFields(clazz)
				.filter(f -> f.isMandatory())
				.map(f -> f.getType())
				.iterator());
	}
	
	public List<Class<?>> getOptionalDependencies(Class<?> clazz) {
		return ImmutableList.copyOf(injectableFields(clazz)
				.filter(f -> !f.isMandatory())
				.map(f -> f.getType())
				.iterator());
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
	
	private Stream<Field> nonStaticFields(Class<?> clazz) {
		return declaredClasses(clazz)
			.flatMap(c -> Stream.of(c.getDeclaredFields()))
			.filter(f -> !Modifier.isStatic(f.getModifiers()));
	}
	
	private Stream<FieldInjector> injectableFields(Class<?> clazz) {
		return nonStaticFields(clazz).map(f -> {
			if (f.isAnnotationPresent(javax.inject.Inject.class)) {
				boolean mandatory = !f.isAnnotationPresent(javax.annotation.Nullable.class);
				if (!f.isAnnotationPresent(javax.inject.Named.class)) {
					return new FieldInjector(f, mandatory, null);
				
				} else {
					Named named = f.getAnnotation(javax.inject.Named.class);
					return new FieldInjector(f, mandatory, named.value());
				}
			} 
			//else if ... com.google.inject.Inject from Guice have the optional parameter
			else return null;
		}).filter(x -> x != null);
	}	
	
	private Optional<Method> getMethod(Object instance, Class<? extends Annotation> annotationClass) {
		Method[] methods = instance.getClass().getDeclaredMethods();
		return Stream.of(methods)
				.filter(m -> !Modifier.isStatic(m.getModifiers()))
				.filter(m -> m.isAnnotationPresent(annotationClass))
				.findFirst();
	}	
	
	private Object invoke(Object instance, Method method, Object[] args) {
		try {
			return method.invoke(instance, args);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		}
	}
	
	private void doPreDestroy(Object instance) {
		Optional<Method> optional = getMethod(instance, PreDestroy.class);
		if (optional.isPresent()) {
			Method method = optional.get();
			method.setAccessible(true);
			invoke(instance, method, null);
		}
	}

	private void doPostConstruct(Object instance) {
		Optional<Method> optional = getMethod(instance, PostConstruct.class);
		if (optional.isPresent()) {
			Method method = optional.get();
			method.setAccessible(true);
			invoke(instance, method, null);
		}
	}
	
	private void doInject(Object instance) {
		injectableFields(instance.getClass()).forEach(fi -> {
			
			String name = fi.getNamed().orElse(fi.getName());
			Object value = resolver.resolve(instance, fi.getType(), name);
			if (value != null) {
				fi.inject(instance, value);

			} else if (fi.isMandatory()) /* field is null and mandatory */ { 
				throw new RuntimeException(String.format(
						"Cannot resolve property %s %s on bean %s", 
						fi.getType(), fi.getName(), instance));
			}
		});
	}
	
	

}

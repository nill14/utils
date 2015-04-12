package com.github.nill14.utils.init.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

public class ParameterTypeInjectionDescriptor<T> implements IParameterType<T> {
	
	
	private static final boolean isGuiceInjectPresent = ReflectionUtils.isClassPresent("com.google.inject.Inject");
	private static final boolean isGuiceNamedPresent = ReflectionUtils.isClassPresent("com.google.inject.name.Named");
	
	private final Type type;
	private final TypeToken<T> typeToken;
	private final Optional<String> named;

	//There are multiple annotations in Java8 but they are always packed in another annotation
	private final ImmutableMap<Class<?>, Annotation> qualifiers;
	private final ImmutableMap<Class<?>, Annotation> annotations;
	private final boolean optional;
	private final boolean nullable;
	
	@SuppressWarnings("unchecked")
	public ParameterTypeInjectionDescriptor(Type type, Annotation[] annotations) {
		this.type = type;
		typeToken = (TypeToken<T>) TypeToken.of(type);

		Map<Class<? extends Annotation>, Annotation> qualifiers = AnnotationScanner.findAnnotations(annotations, javax.inject.Qualifier.class);
//		Map<Class<? extends Annotation>, Annotation> bindingAnnotations = AnnotationScanner.findAnnotations(annotations, com.google.inject.BindingAnnotation.class);
		ImmutableMap.Builder<Class<?>, Annotation> builder = ImmutableMap.builder();
		this.qualifiers = builder.putAll(qualifiers)/*.putAll(bindingAnnotations)*/.build();
		this.annotations = ImmutableMap.copyOf(AnnotationScanner.indexAnnotations(annotations));
		
		javax.inject.Named named = (javax.inject.Named ) this.annotations.get(javax.inject.Named.class);
		//see Names.named(String)
		Optional<String> name = Optional.ofNullable(named).map(n -> n.value());
		Optional<String> name2 = isGuiceNamedPresent ? OptionalGuiceDependency.getNamed(this.annotations): Optional.empty();
		this.named = name.isPresent() ? name : name2;
		
		Nullable nullable = (Nullable) this.annotations.get(javax.annotation.Nullable.class);
		
		Optional<Boolean> googleInject = isGuiceInjectPresent ? 
				OptionalGuiceDependency.isOptionalInject(this.annotations) : null;
		this.nullable = nullable != null || googleInject != null && googleInject.get() == true;
		
		optional = java.util.Optional.class.isAssignableFrom(typeToken.getRawType())
				|| com.google.common.base.Optional.class.isAssignableFrom(typeToken.getRawType());
	}
	
	@Override
	public boolean isParametrized() {
		return typeToken.getType() instanceof ParameterizedType;
	}

	@Override
	public TypeToken<?> getFirstParamToken() {
		if (type instanceof ParameterizedType) {
			Type argType = ((ParameterizedType) type).getActualTypeArguments()[0];
			return TypeToken.of(argType);
		}
		throw new IllegalStateException();
	}

	@Override
	public Type[] getParameterTypes() {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments();
		}
		throw new IllegalStateException();
	}
	
	@Override
	public Type getGenericType() {
		return type;
	}

	@Override
	public Class<?> getRawType() {
		return typeToken.getRawType();
	}
	
	@Override
	public TypeToken<T> getToken() {
		return typeToken;
	}
	
	@Override
	public Optional<String> getNamed() {
		return named;
	}
	
	@Override
	public String toString() {
		return typeToken.toString();
	}
	
	@Override
	public Collection<Annotation> getQualifiers() {
		return qualifiers.values();
	}

	@Override
	public Collection<Annotation> getAnnotations() {
		return annotations.values();
	}
	
	@Override
	public Optional<Annotation> getAnnotation(
			Class<? extends Annotation> annotation) {
		
		return Optional.ofNullable(annotations.get(annotation));
	}
	
	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}
	
	private static final class OptionalGuiceDependency {
		
		public static Optional<String> getNamed(ImmutableMap<Class<?>, Annotation> annotations) {
			com.google.inject.name.Named named = (com.google.inject.name.Named) annotations.get(com.google.inject.name.Named.class);
			return Optional.ofNullable(named).map(com.google.inject.name.Named::value);
		}
		
		public static Optional<Boolean> isOptionalInject(ImmutableMap<Class<?>, Annotation> annotations) {
			com.google.inject.Inject inject = (com.google.inject.Inject) annotations.get(com.google.inject.Inject.class);
			if (inject != null) {
				return Optional.of(inject.optional());
			}
			return null;
		}
	}
}

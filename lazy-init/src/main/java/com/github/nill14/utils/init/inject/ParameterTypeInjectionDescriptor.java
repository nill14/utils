package com.github.nill14.utils.init.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

public class ParameterTypeInjectionDescriptor implements IParameterType {
	
	
	private static final boolean isGuicePresent = ReflectionUtils.isClassPresent("com.google.inject.Inject");
	
	private final Type type;
	private final TypeToken<?> typeToken;
	private final Optional<String> named;

	private final @Nullable Annotation qualifier;
	private final ImmutableMap<Class<? extends Annotation>, Annotation> annotations;
	private final boolean optional;
	private final boolean collection; 
	private final boolean nullable;
	private final Class<?> declaringClass;
	

	private static <T> ParameterTypeInjectionDescriptor ofFirstParam(IParameterType type) {
		TypeToken<?> firstParamToken = type.getFirstParamToken();
		ImmutableMap<Class<? extends Annotation>, Annotation> annotations = AnnotationScanner.indexAnnotations(type.getAnnotations().stream());
				
		return new ParameterTypeInjectionDescriptor(firstParamToken.getType(), firstParamToken, type.getNamed(), type.getQualifier(), annotations, type.getDeclaringClass());
	}
	
	public static <T> ParameterTypeInjectionDescriptor of(BindingKey<T> bindingKey) {
		Annotation qualifier = bindingKey.getQualifier();
		
		Optional<String> optionalNamed = qualifier instanceof javax.inject.Named ? 
				Optional.ofNullable(((javax.inject.Named ) qualifier).value()) : Optional.empty();
		
		ImmutableMap<Class<? extends Annotation>, Annotation> annotations = qualifier != null ? 
				AnnotationScanner.indexAnnotations(Stream.of(qualifier)) : ImmutableMap.of();
				
		return new ParameterTypeInjectionDescriptor(bindingKey.getGenericType(), bindingKey.getToken(), optionalNamed, qualifier, annotations, null);
	}
	
	public static ParameterTypeInjectionDescriptor of(Type type, Annotation[] annotations, Member member, /*@Nullable Annotation qualifier,*/ @Nullable Class<?> declaringClass) {
		Annotation qualifier = null;
		ImmutableMap<Class<? extends Annotation>, Annotation> annotations2 = ImmutableMap.copyOf(AnnotationScanner.indexAnnotations(annotations));
		Stream<Annotation> stream = Stream.of(annotations).filter(a -> a.annotationType().isAnnotationPresent(javax.inject.Qualifier.class));
		if (isGuicePresent) {
			stream = OptionalGuiceDependency.appendNamed(stream, annotations2);
		}
		
		Map<Class<? extends Annotation>, Annotation> qualifiers = AnnotationScanner.indexAnnotations(stream);
		
		if (qualifiers.size() > 1) {
			throw new RuntimeException("Specification expects at most one qualifier: " + member); 
			
		} else if (qualifiers.size() == 1){
			qualifier = qualifiers.values().iterator().next();
		}
				
		Optional<String> optionalNamed;
		javax.inject.Named named = (javax.inject.Named ) annotations2.get(javax.inject.Named.class);
		if (named != null) {
			optionalNamed = Optional.of(named.value());
		
		} else {
			//see Names.named(String)
			optionalNamed = isGuicePresent ? OptionalGuiceDependency.getOptionalNamed(annotations2): Optional.empty();
		}
	
		return new ParameterTypeInjectionDescriptor(type, TypeToken.of(type), optionalNamed, qualifier, annotations2, declaringClass);
	}
	
	
	
	
	private ParameterTypeInjectionDescriptor(Type type, TypeToken<?> typeToken, Optional<String> named,
			@Nullable Annotation qualifier, ImmutableMap<Class<? extends Annotation>, Annotation> annotations, 
			@Nullable Class<?> declaringClass) {
		this.type = type;
		this.typeToken = typeToken;
		this.named = named;
		this.qualifier = qualifier;
		this.annotations = annotations;
		this.declaringClass = declaringClass;
		
		optional = java.util.Optional.class.isAssignableFrom(typeToken.getRawType())
				|| com.google.common.base.Optional.class.isAssignableFrom(typeToken.getRawType());
		
		Nullable nullable = (Nullable) annotations.get(javax.annotation.Nullable.class);
		if (nullable != null) {
			this.nullable = true;
		} else {
			Optional<Boolean> googleInject = isGuicePresent ? 
				OptionalGuiceDependency.isOptionalInject(annotations) : null;
				this.nullable = googleInject != null && googleInject.get() == true;
		}
		
		collection = Iterable.class.isAssignableFrom(typeToken.getRawType());
		
	}

	@Override
	public boolean isParametrized() {
		return typeToken.getType() instanceof ParameterizedType;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return declaringClass;
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
	public IParameterType getFirstParamType() {
		if (type instanceof ParameterizedType) {
			return ofFirstParam(this);
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
	public TypeToken<?> getToken() {
		return typeToken;
	}
	
	@Override
	public BindingKey<?> getBindingKey() {
		return BindingKey.of(typeToken, qualifier);
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
	public Annotation getQualifier() {
		return qualifier;
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
	public boolean isCollection() {
		return collection;
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}
	
	private static final class OptionalGuiceDependency {
		
		public static Stream<Annotation> appendNamed(Stream<Annotation> qualifiers, ImmutableMap<Class<? extends Annotation>, Annotation> annotations) {
			com.google.inject.name.Named named = (com.google.inject.name.Named) annotations.get(com.google.inject.name.Named.class);
			if (named != null) {
				return Stream.concat(qualifiers, Stream.of(named));
			}
			return qualifiers;
		}
		
		public static Optional<String> getOptionalNamed(ImmutableMap<Class<? extends Annotation>, Annotation> qualifiers) {
			com.google.inject.name.Named gnamed = (com.google.inject.name.Named) qualifiers.get(com.google.inject.name.Named.class);
			if (gnamed != null) {
				return Optional.of(gnamed.value());
			}
			return Optional.empty();
		}
		
		public static Optional<Boolean> isOptionalInject(ImmutableMap<Class<? extends Annotation>, Annotation> annotations) {
			com.google.inject.Inject inject = (com.google.inject.Inject) annotations.get(com.google.inject.Inject.class);
			if (inject != null) {
				return Optional.of(inject.optional());
			}
			return null;
		}
	}
}

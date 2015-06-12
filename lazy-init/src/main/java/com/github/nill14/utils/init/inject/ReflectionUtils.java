package com.github.nill14.utils.init.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.Binder;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.github.nill14.utils.init.binding.target.ProvidesMethodBindingTarget;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.github.nill14.utils.init.meta.Provides;
import com.github.nill14.utils.init.scope.PrototypeScope;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public enum ReflectionUtils {
	;
	
	@SuppressWarnings({ "unchecked" })
	public static <T> TypeToken<T> getProviderReturnTypeToken(Class<? extends Provider<? extends T>> providerClass) {
		try {
			return (TypeToken<T>) TypeToken.of(providerClass.getMethod("get").getGenericReturnType());
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static <T> TypeToken<T> getProviderReturnTypeToken(Provider<? extends T> provider) {
		return getProviderReturnTypeToken((Class<? extends Provider<? extends T>>) provider.getClass());
	}
	
	@SuppressWarnings({ "unchecked" })
	public static <T> TypeToken<T> getProviderReturnTypeToken(TypeToken<? extends Provider<? extends T>> providerType) {
		return getProviderReturnTypeToken((Class<? extends Provider<? extends T>>)providerType.getRawType());
	}
	
	public static boolean isClassPresent(String name) {
		try {
			Class.forName(name);
			return true;
		} catch(ClassNotFoundException e) {
			return false;
		}
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<BindingImpl<?>> scanProvidesBindings(Binder binder, Object module) {
		//avoid confusion with importing the wrong import
		boolean isGuicePresent = ReflectionUtils.isClassPresent("com.google.inject.Provides");
		
		List<BindingImpl<?>> result = Lists.newArrayList();
		Stream<Method> stream = ReflectionUtils.getInstanceMethods(module.getClass());
		
		Iterable<Method> iterable = stream::iterator;
		for (Method m : iterable) {
			
			if (m.isAnnotationPresent(Provides.class) || (isGuicePresent && OptionalGuiceDependency.isGuiceProvidesPresent(m))) {
				TypeToken typeToken = TypeToken.of(m.getGenericReturnType());
				ProvidesMethodBindingTarget<Object> target = new ProvidesMethodBindingTarget<>(m, module);
				
				Annotation qualifier = AnnotationScanner.findQualifier(m).orElse(null);
				Optional<Annotation> scopeAnnotation = AnnotationScanner.findScope(m);
				
				
				BindingKey type = BindingKey.of(typeToken, qualifier);
				IScope scope = scopeAnnotation.map(a -> binder.getScope(a.annotationType())).orElse(PrototypeScope.instance());
				
				BindingImpl binding = new BindingImpl(type, target, scope, module);				
				result.add(binding);
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param clazz
	 * @return All classes including self, excluding interfaces and Object.class
	 */
	public static <T> Stream<Class<? super T>> getSuperClasses(Class<T> clazz) {
		return TypeToken.of(clazz)
				.getTypes()
				.stream()
				.filter(t -> !t.getRawType().isInterface())
				.filter(t -> !Object.class.equals(t.getRawType()))
				.map(t -> t.getRawType());
	}
	
	
	/**
	 * Stream of non-static methods declared on the class or it's super-classes.
	 * Methods declared on Object are excluded unless they are overridden.
	 * Methods are ordered from subclass to superclass
	 * @param clazz
	 * @return Stream of non-static methods declared on the class or it's super-classes (excluding Object-declared methods).
	 */
	public static <T> Stream<Method> getInstanceMethods(Class<T> clazz) {
		return getSuperClasses(clazz)
				.flatMap(cls -> Stream.of(cls.getDeclaredMethods()))
				.filter(m -> !Modifier.isStatic(m.getModifiers()));
	}
	

	
	private static final class OptionalGuiceDependency {
		
		public static boolean isGuiceProvidesPresent(AnnotatedElement element) {
			return element.isAnnotationPresent(com.google.inject.Provides.class);
		}
	}	
}
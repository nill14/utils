package com.github.nill14.utils.init.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

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
		
		List<BindingImpl<?>> result = Lists.newArrayList();
		Class<?> moduleClass = module.getClass();
		for (Method m : moduleClass.getDeclaredMethods()) {
			
			if (m.isAnnotationPresent(Provides.class)) {
				TypeToken typeToken = TypeToken.of(m.getGenericReturnType());
				ProvidesMethodBindingTarget<Object> target = new ProvidesMethodBindingTarget<>(m, module);
				
				Optional<Annotation> qualifier = AnnotationScanner.findQualifier(m.getAnnotations(), m);
				Optional<Annotation> scopeAnnotation = AnnotationScanner.findScope(m.getAnnotations(), m);
				
				
				
				BindingKey type = qualifier.isPresent() ? 
						BindingKey.of(typeToken, qualifier.get()) : 
							BindingKey.of(typeToken);
				IScope scope = scopeAnnotation.map(a -> binder.getScope(a.annotationType())).orElse(PrototypeScope.instance());
				
				BindingImpl binding = new BindingImpl(type, target, scope, module);				
				result.add(binding);
			}
		}
		
		return result;
	}
}
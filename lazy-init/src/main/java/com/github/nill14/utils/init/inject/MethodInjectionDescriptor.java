package com.github.nill14.utils.init.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import com.github.nill14.utils.init.api.IMemberDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class MethodInjectionDescriptor implements IMemberDescriptor {

	private final Method method;
	private final ImmutableList<IParameterType> parameterTypes;
	private final ImmutableMap<Class<? extends Annotation>, Annotation> annotations;
	private final boolean optionalInject;

	public MethodInjectionDescriptor(Method m) {
		this.method = m;
		boolean optionalInject = true;
		
		Annotation[][] paramAnnotations = method.getParameterAnnotations();
		Type[] paramTypes = method.getGenericParameterTypes();
		Builder<IParameterType> builder = ImmutableList.builder();
		
		for (int i = 0; i < method.getParameterCount(); i++) {
			ParameterTypeInjectionDescriptor td = new ParameterTypeInjectionDescriptor(paramTypes[i], paramAnnotations[i]);
			builder.add(td);
			optionalInject |= td.isNullable();
		}
		
		parameterTypes = builder.build();
		this.optionalInject = optionalInject;
		
		this.annotations = ImmutableMap.copyOf(AnnotationScanner.indexAnnotations(method.getAnnotations()));
	}

	@Override
	public Optional<Annotation> getAnnotation(Class<? extends Annotation> annotation) {
		return Optional.ofNullable(annotations.get(annotation));
	}

	@Override
	public Collection<Annotation> getAnnotations() {
		return annotations.values();
	}

	@Override
	public Collection<IParameterType> getParameterTypes() {
		return parameterTypes;
	}

	@Override
	public Object invoke(Object receiver, Object... args) throws InvocationTargetException, ReflectiveOperationException  {
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		return method.invoke(receiver, args);
	}
	
	@Override
	public boolean isOptionalInject() {
		return optionalInject;
	}
	
	@Override
	public String toString() {
		return method.toGenericString();
	}
}

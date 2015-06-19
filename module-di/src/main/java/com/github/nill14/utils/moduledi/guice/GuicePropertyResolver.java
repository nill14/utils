package com.github.nill14.utils.moduledi.guice;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;

import com.github.nill14.utils.init.api.ICallerContext;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class GuicePropertyResolver extends AbstractPropertyResolver {

	private final Injector injector;
	
	public GuicePropertyResolver(Injector injector) {
		this.injector = injector;
	}

	@Override
	protected Object findByName(String name, IParameterType type, ICallerContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object findByType(IParameterType type, ICallerContext context) {
		Class<?> clazz = type.getRawType();
		return injector.getInstance(clazz);
	}

	@Override
	protected Collection<?> findAllByType(IParameterType type, ICallerContext context) {
		TypeLiteral<Object> literal = (TypeLiteral<Object>) TypeLiteral.get(type.getGenericType());
		return injector.findBindingsByType(literal).stream()
				.map(b -> b.getProvider().get())
				.collect(Collectors.toList());
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier, ICallerContext context) {
		return injector.getInstance(Key.get(type.getRawType(), qualifier));
	}

//	@Override
//	public Object resolve(Object pojo, IType type) {
//		Injector injector = null;
//		injector.getInstance(key)
//		// TODO Auto-generated method stub
//		return null;
//	}

}

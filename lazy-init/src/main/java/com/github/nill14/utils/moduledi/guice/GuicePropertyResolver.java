package com.github.nill14.utils.moduledi.guice;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

public class GuicePropertyResolver extends AbstractPropertyResolver {

	private final Injector injector;
	
	public GuicePropertyResolver(Injector injector) {
		this.injector = injector;
	}

	@Override
	protected Provider<?> findByName(Object pojo, String name, Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Provider<?> findByType(Object pojo, IParameterType<?> type, Class<?> clazz) {
		return injector.getProvider(clazz);
	}

	@Override
	protected Collection<?> findAllByType(Object pojo, Class<?> type) {
		TypeLiteral<Object> literal = (TypeLiteral<Object>) TypeLiteral.get(type);
		return injector.findBindingsByType(literal).stream()
				.map(b -> b.getProvider().get())
				.collect(Collectors.toList());
	}

	@Override
	protected Provider<?> findByQualifier(Object pojo, Class<?> type, Annotation qualifier) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public Object resolve(Object pojo, IType type) {
//		Injector injector = null;
//		injector.getInstance(key)
//		// TODO Auto-generated method stub
//		return null;
//	}

}

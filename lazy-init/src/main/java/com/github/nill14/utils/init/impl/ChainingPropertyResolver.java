package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public final class ChainingPropertyResolver extends AbstractPropertyResolver {
	
	private final CopyOnWriteArrayList<AbstractPropertyResolver> items;
	
	public ChainingPropertyResolver(AbstractPropertyResolver... resolvers) {
		items = new CopyOnWriteArrayList<>(resolvers);
	}
	
	public ChainingPropertyResolver(ImmutableList<AbstractPropertyResolver> resolvers) {
		items = new CopyOnWriteArrayList<>(resolvers);
	}
	
	public void insert(AbstractPropertyResolver extraResolver) {
		if (extraResolver instanceof ChainingPropertyResolver) {
			throw new IllegalArgumentException();
		}
		items.add(0, extraResolver);
	}

	public void append(AbstractPropertyResolver extraResolver) {
		if (extraResolver instanceof ChainingPropertyResolver) {
			throw new IllegalArgumentException();
		}
		items.add(extraResolver);
	}
	
	public void remove(IPropertyResolver extraResolver) {
		items.remove(extraResolver);
	}
	

	@Override
	protected Object findByName(String name, IParameterType type) {
		for (AbstractPropertyResolver resolver : items) {
			Object result = resolver.findByName(name, type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	protected Object findByType(IParameterType type) {
		for (AbstractPropertyResolver resolver : items) {
			Object result = resolver.findByType(type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	protected Collection<?> findAllByType(IParameterType type) {
		for (AbstractPropertyResolver resolver : items) {
			Collection<?> result = resolver.findAllByType(type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier) {
		for (AbstractPropertyResolver resolver : items) {
			Object result = resolver.findByQualifier(type, qualifier);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}

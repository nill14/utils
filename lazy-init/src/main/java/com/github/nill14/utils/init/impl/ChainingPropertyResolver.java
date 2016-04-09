package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public final class ChainingPropertyResolver extends AbstractPropertyResolver {
	
	private final CopyOnWriteArrayList<AbstractPropertyResolver> items;
	
	public ChainingPropertyResolver() {
		items = new CopyOnWriteArrayList<>();
	}
	
	public ChainingPropertyResolver(List<AbstractPropertyResolver> resolvers, ChainingPojoInitializer initializer) {
		super(initializer);
		items = new CopyOnWriteArrayList<>(resolvers);
	}
	
	public void insert(AbstractPropertyResolver extraResolver) {
		if (extraResolver instanceof ChainingPropertyResolver) {
			throw new IllegalArgumentException();
		}
		if (extraResolver.resolver != this) {
			throw new IllegalStateException();
		}
		items.add(0, extraResolver);
	}

	public void append(AbstractPropertyResolver extraResolver) {
		if (extraResolver instanceof ChainingPropertyResolver) {
			throw new IllegalArgumentException();
		}
//		if (extraResolver.resolver != this) {
//			throw new IllegalStateException();
//		}
		items.add(extraResolver);
	}
	
	public void remove(IPropertyResolver extraResolver) {
		items.remove(extraResolver);
	}
	

	@Override
	protected Object findByName(String name, IParameterType type, CallerContext context) {
		for (AbstractPropertyResolver resolver : items) {
			Object result = resolver.findByName(name, type, context);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	protected Object findByType(IParameterType type, CallerContext context) {
		for (AbstractPropertyResolver resolver : items) {
			Object result = resolver.findByType(type, context);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Collection<?> findAllByType(IParameterType type, CallerContext context) {
		List result = Lists.newArrayList();
		for (AbstractPropertyResolver resolver : items) {
			Collection findAllByType = resolver.findAllByType(type, context);
			result.addAll(findAllByType);
		}
		return result;
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier, CallerContext context) {
		for (AbstractPropertyResolver resolver : items) {
			Object result = resolver.findByQualifier(type, qualifier, context);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	public List<AbstractPropertyResolver> getResolvers() {
		return ImmutableList.copyOf(items);
	}
	
}

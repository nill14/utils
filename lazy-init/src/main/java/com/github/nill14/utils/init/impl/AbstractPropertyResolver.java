package com.github.nill14.utils.init.impl;

import java.util.Collection;
import java.util.Optional;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IType;
import com.github.nill14.utils.init.meta.Wire;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

@SuppressWarnings("serial")
public abstract class AbstractPropertyResolver implements IPropertyResolver {
	


	@Override
	public Object resolve(Object pojo, IType type) {
		
		String name = type.getName();
		
		// handle collections, optional
		if (type.isParametrized()) {
			Class<?> rawType = type.getRawType();
			Class<?> paramClass = type.getFirstParamClass();

			if (Optional.class.isAssignableFrom(rawType)) {
				Object result = findByName(pojo, name, paramClass);
				if (result != null) {
					return Optional.of(result);
				}
				if (!type.isNamed()) {
					result = findByType(pojo, paramClass);
					if (result != null) {
						return Optional.of(result);
					}
				}
				return Optional.empty();
			}
			
			if (Iterable.class.isAssignableFrom(rawType)) {
				Collection<?> providers = findAllByType(pojo, paramClass);
				Preconditions.checkNotNull(providers);
				
				if (rawType.isAssignableFrom(ImmutableList.class)) {
					return ImmutableList.copyOf(providers);
				
				} else if (rawType.isAssignableFrom(ImmutableSet.class)) {
					return ImmutableSet.copyOf(providers);
				
				} else if (rawType.isAssignableFrom(ImmutableSortedSet.class)) {
					return ImmutableSortedSet.copyOf(providers);
				}
			}
		}
		
		// find by name
		Object result = findByName(pojo, name, type.getRawType());
		if (result != null) {
			return result;
		}
		
		// find by type
		if (!type.isNamed()) {
			result = findByType(pojo, type.getRawType());
			if (result != null) {
				return result;
			}
		}
		
		if (type.getQualifier(Wire.class).isPresent()) {
			IBeanInjector injector = new BeanInjector(this);
			return injector.wire(type.getRawType());
		}
		
		return null;
	}
	
	protected abstract Object findByName(Object pojo, String name, Class<?> type);
	protected abstract Object findByType(Object pojo, Class<?> type);
	protected abstract Collection<?> findAllByType(Object pojo, Class<?> type);

}

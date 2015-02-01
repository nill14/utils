package com.github.nill14.utils.init.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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
				Object result = findByName(name, paramClass);
				if (result != null) {
					return Optional.of(result);
				}
				result = findByType(paramClass);
				if (result != null) {
					return Optional.of(result);
				}
				return Optional.empty();
			}
			
			if (Collection.class.isAssignableFrom(rawType)) {
				Collection<?> providers = findAllByType(paramClass);
				Preconditions.checkNotNull(providers);
				
				if (Set.class.isAssignableFrom(rawType)) {
					return ImmutableSet.copyOf(providers);
				} else {
					return ImmutableList.copyOf(providers);
				}
			}
		}
		
		// find by name
		Object result = findByName(name, type.getRawType());
		if (result != null) {
			return result;
		}
		
		// find by type
		result = findByType(type.getRawType());
		if (result != null) {
			return result;
		}
		
		return null;
	}
	
	protected abstract Object findByName(String name, Class<?> type);
	protected abstract Object findByType(Class<?> type);
	protected abstract Collection<?> findAllByType(Class<?> type);

}

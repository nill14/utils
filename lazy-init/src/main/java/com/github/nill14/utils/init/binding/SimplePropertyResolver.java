package com.github.nill14.utils.init.binding;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SimplePropertyResolver implements IPropertyResolver {
	
	private static final long serialVersionUID = 6151314354173527220L;
	
	private final ImmutableMap<BindingKey<?>, BindingImpl<?>> bindings;

	public SimplePropertyResolver(ImmutableList<BindingImpl<?>> bindings) {
		ImmutableMap.Builder<BindingKey<?>, BindingImpl<?>> builder = ImmutableMap.builder();
		for (BindingImpl<?> binding : bindings) {
			BindingKey<?> key = binding.getBindingKey();
			builder.put(key, binding);
			if (key.getQualifier() != null) {
				builder.put(BindingKey.of(key.getToken()), binding);
				//put also bindingKey without qualifier	
			}
		}
		this.bindings = builder.build();
	}

	@Override
	public Object resolve(IParameterType type) {
		BindingKey<?> bindingKey = type.getBindingKey();
		
		if (bindings.containsKey(bindingKey)) {
			BindingImpl<?> binding = bindings.get(bindingKey);
			IScope scope = binding.getScope();
			
			Provider<?> scopedProvider = scope.scope(bindingKey, () -> {
				return null;
			});
			
			return scopedProvider.get();
		} else {
			return doPrototype(type);
		}
	}
	
	private Object doPrototype(IParameterType type) {
		return null;
	}

}

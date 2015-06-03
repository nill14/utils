package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Provider;

import com.github.nill14.utils.annotation.Experimental;
import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.target.InitializingProvider;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Experimental
public class SimplePropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {
	
	private static final long serialVersionUID = 6151314354173527220L;
	
	private final ImmutableMap<BindingKey<?>, BindingImpl<?>> bindings;
	

	public SimplePropertyResolver(ImmutableList<BindingImpl<?>> bindings, ChainingPojoInitializer initializer) {
		super(initializer);
		
		Map<BindingKey<?>, BindingImpl<?>> rawBindings = Maps.newHashMap();
		Map<BindingKey<?>, BindingImpl<?>> map = Maps.newHashMap();
		for (BindingImpl<?> binding : bindings) {
			BindingKey<?> key = binding.getBindingKey();
			map.put(key, binding);
			rawBindings.putIfAbsent(key.withQualifier(null), binding);
		}
		
		//add raw/unqualified bindings if they are not already present
		for (Entry<BindingKey<?>, BindingImpl<?>> entry : rawBindings.entrySet()) {
			map.putIfAbsent(entry.getKey(), entry.getValue());
		}
		
		this.bindings = ImmutableMap.copyOf(map);
	}



	@Override
	protected Object findByName(String name, IParameterType type) {
		return null;
	}

	@Override
	protected Object findByType(IParameterType type) {
		return simple(type);
	}

	@Override
	protected Collection<?> findAllByType(IParameterType type) {
		// TODO Auto-generated method stub
		Object simple = simple(type);
		if (simple != null) {
			return Collections.singleton(simple);
		}
		return Collections.emptyList();
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier) {
		return simple(type);
	}
	
	@SuppressWarnings("unchecked")
	private Object simple(IParameterType type) {
		BindingKey<?> bindingKey = type.getBindingKey();
		
		if (bindings.containsKey(bindingKey)) {
			BindingImpl<?> binding = bindings.get(bindingKey);
			IScope scope = binding.getScope();
			
			InitializingProvider<Object> provider = new InitializingProvider<>(this, 
					(BindingTarget<Object>) binding.getBindingTarget());
			Provider<?> scopedProvider = scope.scope((BindingKey<Object>) bindingKey, provider);
			
			return scopedProvider.get();
		} else {
			return doPrototype(type);
		}
	}

}

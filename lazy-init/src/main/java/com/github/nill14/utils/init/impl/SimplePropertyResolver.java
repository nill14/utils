package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
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
import com.github.nill14.utils.init.binding.target.UnscopedProvider;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

@Experimental
public class SimplePropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {
	
	private static final long serialVersionUID = 6151314354173527220L;
	
	private final ImmutableMap<BindingKey<?>, BindingImpl<?>> bindings;
	private final Multimap<TypeToken<?>, BindingImpl<?>> typeBindings = ArrayListMultimap.create();
	

	public SimplePropertyResolver(ImmutableList<BindingImpl<?>> bindings, ChainingPojoInitializer initializer) {
		super(initializer);
		
		Map<BindingKey<?>, BindingImpl<?>> rawBindingCandidates = Maps.newHashMap();
		Map<BindingKey<?>, BindingImpl<?>> map = Maps.newHashMap();
		for (BindingImpl<?> binding : bindings) {
			BindingKey<?> key = binding.getBindingKey();
			boolean occupied = map.putIfAbsent(key, binding) != null; //TODO scope?
			
			if (occupied && key.getQualifier() != null) {
				throw new RuntimeException(String.format("Duplicate key: %s", key));
				
			} else if (occupied) {
				binding = binding.keyWithQualifier(CounterImpl.next());
				key = binding.getBindingKey();
				boolean error = map.putIfAbsent(key, binding) != null;
				if (error) {
					throw new RuntimeException("Duplicate key: " + binding);
				}
			}
			
			typeBindings.put(key.getToken(), binding);
			rawBindingCandidates.putIfAbsent(key.withQualifier(null), binding);
		}
		
		//add raw/unqualified bindings if they are not already present
		for (Entry<BindingKey<?>, BindingImpl<?>> entry : rawBindingCandidates.entrySet()) {
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
		Collection<BindingImpl<?>> bindings = typeBindings.get(type.getToken());
		ImmutableList.Builder<Object> builder = ImmutableList.builder();
		for (BindingImpl<?> binding : bindings) {
			IScope scope = binding.getScope();
			
			//TODO provide scope with info about "calling" scope
			UnscopedProvider<Object> provider = new UnscopedProvider<>(this, 
					(BindingTarget<Object>) binding.getBindingTarget());
			Provider<?> scopedProvider = scope.scope((BindingKey<Object>) binding.getBindingKey(), provider);
			builder.add(scopedProvider.get());
		}
		return builder.build();
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
			
			UnscopedProvider<Object> provider = new UnscopedProvider<>(this, 
					(BindingTarget<Object>) binding.getBindingTarget());
			Provider<?> scopedProvider = scope.scope((BindingKey<Object>) bindingKey, provider);
			
			return scopedProvider.get();
		} else {
			return doPrototype(type);
		}
	}

}

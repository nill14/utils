package com.github.nill14.utils.init.binding;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Provider;

import org.testng.collections.Maps;

import com.github.nill14.utils.annotation.Experimental;
import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.target.InitializingProvider;
import com.github.nill14.utils.init.impl.BeanInjector;
import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.PojoInjectionFactory;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

@Experimental
public class SimplePropertyResolver implements IPropertyResolver {
	
	private static final long serialVersionUID = 6151314354173527220L;
	
	private final ImmutableMap<BindingKey<?>, BindingImpl<?>> bindings;
	
	private final IBeanInjector beanInjector = new BeanInjector(this);
	private final ChainingPojoInitializer initializer;

	public SimplePropertyResolver(ImmutableList<BindingImpl<?>> bindings, ChainingPojoInitializer initializer) {
		this.initializer = initializer;
		
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

	@SuppressWarnings("unchecked")
	@Override
	public Object resolve(IParameterType type) {
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
	
	private Object doPrototype(IParameterType type) {
		IBeanDescriptor<Object> typeDescriptor = new PojoInjectionDescriptor<>(type);
		if (typeDescriptor.canBeInstantiated()) {
			return new PojoInjectionFactory<>(typeDescriptor).newInstance(this);
		}
		return null;
	}

	@Override
	public IBeanInjector toBeanInjector() {
		return beanInjector;
	}

	@Override
	public void initializeBean(Object instance) {
		IPropertyResolver resolver = this;
		//TODO fix this hack
		IPojoInitializer.standard().init(resolver, new IPojoFactory<Object>() {

			@Override
			public Object newInstance(IPropertyResolver resolver) {
				throw new UnsupportedOperationException();
			}

			@Override
			public TypeToken<Object> getType() {
				throw new UnsupportedOperationException();
			}

			@SuppressWarnings("unchecked")
			@Override
			public IBeanDescriptor<Object> getDescriptor() {
				return new PojoInjectionDescriptor<>((Class<Object>) instance.getClass());
			}
		}, instance);
	}

	@Override
	public List<IPojoInitializer> getInitializers() {
		return initializer.getItems();
	}

}

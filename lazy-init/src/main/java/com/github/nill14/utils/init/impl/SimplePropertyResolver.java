package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.Binding;
import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.target.LinkedBindingTarget;
import com.github.nill14.utils.init.scope.IScopeStrategy;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

public class SimplePropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {
	
	private static final long serialVersionUID = 6151314354173527220L;
	
	private final ImmutableMap<BindingKey<?>, Binding<?>> bindings;
	private final Multimap<TypeToken<?>, Binding<?>> typeBindings;
	private final ImmutableMap<BindingTarget<?>, IPojoFactory<?>> bindingFactories;
	

	public SimplePropertyResolver(ImmutableList<Binding<?>> bindings, ChainingPojoInitializer initializer) {
		super(initializer);
		this.bindings = BinderUtils.prepareAndIndexBindings(bindings);
		this.typeBindings = BinderUtils.indexTypeBindings(this.bindings.values());
		this.bindingFactories = BinderUtils.indexFactories(this.bindings.values());
	}

	
	public SimplePropertyResolver(ImmutableList<Binding<?>> bindings, ChainingPropertyResolver parent) {
		super(parent);
		this.bindings = BinderUtils.prepareAndIndexBindings(bindings);
		this.typeBindings = BinderUtils.indexTypeBindings(this.bindings.values());
		this.bindingFactories = BinderUtils.indexFactories(this.bindings.values());
	}




	@Override
	protected Object findByName(String name, IParameterType type, CallerContext context) {
		return null;
	}

	@Override
	protected Object findByType(IParameterType type, CallerContext context) {
		return resolveSingle(type, context);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<?> findAllByType(IParameterType type, CallerContext context) {
		Collection<Binding<?>> bindings = typeBindings.get(type.getToken());
		ImmutableList.Builder<Object> builder = ImmutableList.builder();
		for (Binding<?> binding : bindings) {
			binding = getLinkedBinding(binding);
			BindingKey<?> bindingKey = binding.getBindingKey();

			
			IScope scope = binding.getScopeStrategy().resolveScope();
			IPojoFactory<Object> pojoFactory = (IPojoFactory<Object>) bindingFactories.get(binding.getBindingTarget());
			
			UnscopedProvider<Object> provider = new UnscopedProvider<>(resolver, bindingKey, pojoFactory, context); 
			Provider<?> scopedProvider = scope.scope((BindingKey<Object>) bindingKey, provider, context.getScopeContext());
			Object element = scopedProvider.get();
			if (element != null) {
				builder.add(element);
			}
		}
		return builder.build();
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier, CallerContext context) {
		return resolveSingle(type, context);
	}
	
	@SuppressWarnings("unchecked")
	private Object resolveSingle(IParameterType type, CallerContext context) {
		BindingKey<?> bindingKey = type.getBindingKey();
		
		if (bindings.containsKey(bindingKey)) {
			Binding<?> binding = bindings.get(bindingKey);
			binding = getLinkedBinding(binding);
			bindingKey = binding.getBindingKey();

			IScope scope = binding.getScopeStrategy().resolveScope();
			IPojoFactory<Object> pojoFactory = (IPojoFactory<Object>) bindingFactories.get(binding.getBindingTarget());
			
			UnscopedProvider<Object> provider = new UnscopedProvider<>(resolver, bindingKey, pojoFactory, context);
			Provider<?> scopedProvider = scope.scope((BindingKey<Object>) bindingKey, provider, context.getScopeContext());
			
			return scopedProvider.get();
		} else {
//			return doPrototype(type);
			return null; //prototyping might eventually be done by ChainingPropertyResolver
		}
	}
	
	protected Binding<?> getLinkedBinding(Binding<?> binding) {
		IScopeStrategy scope = binding.getScopeStrategy();
		
		if (binding.getBindingTarget() instanceof LinkedBindingTarget) {
			BindingKey<?> bindingKey2 = ((LinkedBindingTarget<?>) binding.getBindingTarget()).getBindingKey();
			Binding<?> binding2 = bindings.get(bindingKey2);
			if (scope.equals(binding2.getScopeStrategy())) {
				if (binding2.getBindingTarget() instanceof LinkedBindingTarget) {
					return getLinkedBinding(binding2);
				}
				
				return binding2;
			}
			else {
				throw new RuntimeException(String.format("LinkedBinding %s without target %s", binding, bindingKey2));
			}
		}
		return binding;
	}
	

	/**
	 * 
	 * @return a map where key is a dependency (e.g. @Inject) and value is whether the dependency is required. 
	 */
	public Map<TypeToken<?>, Boolean> collectDependencies() {
		return BinderUtils.collectDependencies(bindingFactories.values(), bindings);
	}
	
	protected ImmutableMap<BindingKey<?>, Binding<?>> getBindings() {
		return bindings;
	}
	
	protected ImmutableMap<BindingTarget<?>, IPojoFactory<?>> getBindingFactories() {
		return bindingFactories;
	}
}

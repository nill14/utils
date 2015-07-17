package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Provider;

import com.github.nill14.utils.annotation.Experimental;
import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.target.BeanTypeBindingTarget;
import com.github.nill14.utils.init.binding.target.LinkedBindingTarget;
import com.github.nill14.utils.init.binding.target.PojoFactoryBindingTargetVisitor;
import com.github.nill14.utils.init.inject.DependencyUtils;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.github.nill14.utils.init.scope.PrototypeScope;
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
	private final ImmutableMap<BindingTarget<?>, IPojoFactory<?>> bindingFactories;
	

	public SimplePropertyResolver(ImmutableList<BindingImpl<?>> bindings, ChainingPojoInitializer initializer) {
		super(initializer);
		this.bindings = prepareBindings(bindings);
		this.bindingFactories = prepareFactories(this.bindings.values());
	}

	
	public SimplePropertyResolver(ImmutableList<BindingImpl<?>> bindings, ChainingPropertyResolver parent) {
		super(parent);
		this.bindings = prepareBindings(bindings);
		this.bindingFactories = prepareFactories(this.bindings.values());
	}

	private ImmutableMap<BindingTarget<?>, IPojoFactory<?>> prepareFactories(
			Collection<BindingImpl<?>> bindings) {
		
		Set<BindingTarget<?>> targets = bindings.stream()
			.map(b -> b.getBindingTarget())
			.filter(t -> !(t instanceof LinkedBindingTarget))
			.collect(Collectors.toSet());
		
		ImmutableMap.Builder<BindingTarget<?>, IPojoFactory<?>> builder = ImmutableMap.builder();
		PojoFactoryBindingTargetVisitor bindingTargetVisitor = new PojoFactoryBindingTargetVisitor(); 
		for (BindingTarget<?> target : targets) {
			IPojoFactory<?> pojoFactory = target.accept(bindingTargetVisitor);
			builder.put(target, pojoFactory);
		}
		
		return builder.build();
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final ImmutableMap<BindingKey<?>, BindingImpl<?>> prepareBindings(ImmutableList<BindingImpl<?>> bindings)  {
		Map<BindingKey<?>, BindingImpl<?>> rawBindingCandidates = Maps.newHashMap();
		Map<BindingKey<?>, BindingImpl<?>> map = Maps.newHashMap();
		for (BindingImpl<?> binding : bindings) {
			BindingKey<?> key = binding.getBindingKey();
			boolean occupied = map.putIfAbsent(key, binding) != null; //TODO scopes? (identical key, different scope - not possible now)
			
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
			BindingKey<?> rawBindingKey = entry.getKey();
			if (!map.containsKey(rawBindingKey)) {
				BindingImpl<?> binding = entry.getValue();
				BindingKey<?> qualifiedKey = binding.getBindingKey();
				binding = binding.withLinkedBinding((BindingKey) qualifiedKey).keyWithQualifier(null);
				map.put(rawBindingKey, binding);
			}
		}
		
		map = replaceLinkedBindings(map);
		return ImmutableMap.copyOf(map);
	}

	@Override
	protected Object findByName(String name, IParameterType type, CallerContext context) {
		return null;
	}

	@Override
	protected Object findByType(IParameterType type, CallerContext context) {
		return simple(type, context);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<?> findAllByType(IParameterType type, CallerContext context) {
		Collection<BindingImpl<?>> bindings = typeBindings.get(type.getToken());
		ImmutableList.Builder<Object> builder = ImmutableList.builder();
		for (BindingImpl<?> binding : bindings) {
			binding = getLinkedBinding(binding);
			BindingKey<?> bindingKey = binding.getBindingKey();

			IScope scope = context.resolveScope(binding.getScope());
			IPojoFactory<Object> pojoFactory = (IPojoFactory<Object>) bindingFactories.get(binding.getBindingTarget());
			
			//TODO provide scope with info about "calling" scope
			UnscopedProvider<Object> provider = new UnscopedProvider<>(resolver, bindingKey, pojoFactory, context); 
			Provider<?> scopedProvider = scope.scope((BindingKey<Object>) bindingKey, provider);
			Object element = scopedProvider.get();
			if (element != null) {
				builder.add(element);
			}
		}
		return builder.build();
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier, CallerContext context) {
		return simple(type, context);
	}
	
	@SuppressWarnings("unchecked")
	private Object simple(IParameterType type, CallerContext context) {
		BindingKey<?> bindingKey = type.getBindingKey();
		
		if (bindings.containsKey(bindingKey)) {
			BindingImpl<?> binding = bindings.get(bindingKey);
			binding = getLinkedBinding(binding);
			bindingKey = binding.getBindingKey();

			IScope scope = context.resolveScope(binding.getScope());
			IPojoFactory<Object> pojoFactory = (IPojoFactory<Object>) bindingFactories.get(binding.getBindingTarget());
			
			UnscopedProvider<Object> provider = new UnscopedProvider<>(resolver, bindingKey, pojoFactory, context);
			Provider<?> scopedProvider = scope.scope((BindingKey<Object>) bindingKey, provider);
			
			return scopedProvider.get();
		} else {
//			return doPrototype(type);
			return null; //prototyping might eventually be done by ChainingPropertyResolver
		}
	}
	
	protected BindingImpl<?> getLinkedBinding(BindingImpl<?> binding) {
		IScope scope = binding.getScope();
		
		if (binding.getBindingTarget() instanceof LinkedBindingTarget) {
			BindingKey<?> bindingKey2 = ((LinkedBindingTarget<?>) binding.getBindingTarget()).getBindingKey();
			BindingImpl<?> binding2 = bindings.get(bindingKey2);
			if (binding2 != null && binding2.getScope() == scope) {
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<BindingKey<?>, BindingImpl<?>> replaceLinkedBindings(Map<BindingKey<?>, BindingImpl<?>> map) {
		
		Collection<BindingImpl<?>> bindings = map.values();
		for (BindingImpl<?> binding : bindings) {
			BindingImpl targetBinding = scanLinkedBinding(map, binding);
			if (targetBinding.getBindingKey() != binding.getBindingKey()) {
				binding = binding.withLinkedBinding(targetBinding.getBindingKey()).withScope(targetBinding.getScope());
				map.put(binding.getBindingKey(), binding);
			}
		}
		
		return map;
	}

	
	private BindingImpl<?> scanLinkedBinding(Map<BindingKey<?>, BindingImpl<?>> map, BindingImpl<?> binding) {
		if (binding.getBindingTarget() instanceof BeanTypeBindingTarget) {
			TypeToken<?> token = ((BeanTypeBindingTarget<?>) binding.getBindingTarget()).getToken();
			BindingKey<?> bindingKey = BindingKey.of(token);
			BindingImpl<?> linkedBinding = map.get(bindingKey);
			if (binding != linkedBinding && linkedBinding != null && 
					(linkedBinding.getScope() == binding.getScope() || binding.getScope() == PrototypeScope.instance())) {
				return scanLinkedBinding(map, linkedBinding);
			}
		
		} else if (binding.getBindingTarget() instanceof LinkedBindingTarget) {
			return map.get(((LinkedBindingTarget<?>) binding.getBindingTarget()).getBindingKey());
		}
		return binding;
	}	


	/**
	 * 
	 * @return a map where key is a dependency (e.g. @Inject) and value is whether the dependency is required. 
	 */
	@SuppressWarnings("unchecked")
	public Map<TypeToken<?>, Boolean> collectDependencies() {
		return DependencyUtils.collectDependencies(bindingFactories.values(), bindingKey -> {
			BindingImpl<?> binding = bindings.get(bindingKey);
			if (binding == null) {
				IBeanDescriptor<Object> typeDescriptor = new PojoInjectionDescriptor<>((TypeToken<Object>) bindingKey.getToken());
				if (typeDescriptor.canBeInstantiated()) {
					return true;
				}
			}
			return false;
		});
	}
	
	protected ImmutableMap<BindingKey<?>, BindingImpl<?>> getBindings() {
		return bindings;
	}
	
	protected ImmutableMap<BindingTarget<?>, IPojoFactory<?>> getBindingFactories() {
		return bindingFactories;
	}
}

package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.binding.impl.Binding;
import com.github.nill14.utils.init.binding.impl.BindingTarget;
import com.github.nill14.utils.init.binding.target.AnnotatedElementBindingTargetVisitor;
import com.github.nill14.utils.init.binding.target.BeanTypeBindingTarget;
import com.github.nill14.utils.init.binding.target.LinkedBindingTarget;
import com.github.nill14.utils.init.binding.target.PojoFactoryBindingTargetVisitor;
import com.github.nill14.utils.init.inject.DependencyUtils;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.github.nill14.utils.init.scope.AnnotationScopeStrategy;
import com.github.nill14.utils.init.scope.IScopeStrategy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

public enum BinderUtils {
	;
	
	public static <T> Binding<T> scanQualifierAndScope(Binding<T> binding) {
		AnnotatedElementBindingTargetVisitor targetVisitor = new AnnotatedElementBindingTargetVisitor();
		BindingKey<T> bindingKey = binding.getBindingKey();
		boolean isPrototypeScope = binding.getScopeStrategy().isPrototype();
		
		if (bindingKey.getQualifier() == null || isPrototypeScope) {
			AnnotatedElement annotatedElement = binding.getBindingTarget().accept(targetVisitor);
			
			if (bindingKey.getQualifier() == null) {
				Annotation qualifier = AnnotationScanner.findQualifier(annotatedElement).orElse(null);
				binding = binding.keyWithQualifier(qualifier);
			}
			
			if (isPrototypeScope) {
				Annotation scopeAnnotation = AnnotationScanner.findScope(annotatedElement).orElse(null);
				if (scopeAnnotation != null) {
					IScopeStrategy scope = AnnotationScopeStrategy.of(scopeAnnotation.annotationType());
					binding = binding.withScopeStrategy(scope);
				}
			}
		}
		
		return binding;
	}
	

	public static ImmutableMap<BindingTarget<?>, IPojoFactory<?>> indexFactories(
			Collection<Binding<?>> bindings) {
		
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<BindingKey<?>, Binding<?>> replaceLinkedBindings(Map<BindingKey<?>, Binding<?>> map) {
		
		Collection<Binding<?>> bindings = map.values();
		for (Binding<?> binding : bindings) {
			Binding targetBinding = scanLinkedBinding(map, binding);
			if (targetBinding.getBindingKey() != binding.getBindingKey()) {
				binding = binding.withLinkedTarget(targetBinding.getBindingKey()).withScopeStrategy(targetBinding.getScopeStrategy());
				map.put(binding.getBindingKey(), binding);
			}
		}
		
		return map;
	}

	
	public static Binding<?> scanLinkedBinding(Map<BindingKey<?>, Binding<?>> map, Binding<?> binding) {
		if (binding.getBindingTarget() instanceof BeanTypeBindingTarget) {
			TypeToken<?> token = ((BeanTypeBindingTarget<?>) binding.getBindingTarget()).getToken();
			BindingKey<?> bindingKey = BindingKey.of(token);
			Binding<?> linkedBinding = map.get(bindingKey);
			if (binding != linkedBinding && linkedBinding != null && 
					(linkedBinding.getScopeStrategy().equals(binding.getScopeStrategy()) || binding.getScopeStrategy().isPrototype())) {
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
	public static Map<TypeToken<?>, Boolean> collectDependencies(Collection<IPojoFactory<?>> bindingFactories, Map<BindingKey<?>, Binding<?>> bindings) {
		return DependencyUtils.collectDependencies(bindingFactories, bindingKey -> {
			Binding<?> binding = bindings.get(bindingKey);
			if (binding == null) {
				IBeanDescriptor<Object> typeDescriptor = new PojoInjectionDescriptor<>((TypeToken<Object>) bindingKey.getToken());
				if (typeDescriptor.canBeInstantiated()) {
					return true;
				}
			}
			return false;
		});
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final ImmutableMap<BindingKey<?>, Binding<?>> prepareAndIndexBindings(ImmutableList<Binding<?>> bindings)  {
		Map<BindingKey<?>, Binding<?>> rawBindingCandidates = Maps.newHashMap();
		Map<BindingKey<?>, Binding<?>> map = Maps.newHashMap();
		for (Binding<?> binding : bindings) {
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
			
//			typeBindings.put(key.getToken(), binding);
			rawBindingCandidates.putIfAbsent(key.withQualifier(null), binding);
		}
		
		//add raw/unqualified bindings if they are not already present, that is explicitly defined
		for (Entry<BindingKey<?>, Binding<?>> entry : rawBindingCandidates.entrySet()) {
			BindingKey<?> rawBindingKey = entry.getKey();
			if (!map.containsKey(rawBindingKey)) {
				Binding<?> binding = entry.getValue();
				BindingKey<?> qualifiedKey = binding.getBindingKey();
				binding = binding.withLinkedTarget((BindingKey) qualifiedKey).keyWithQualifier(null);
				map.put(rawBindingKey, binding);
			}
		}
		
		map = replaceLinkedBindings(map);
		return ImmutableMap.copyOf(map);
	}	
	
	
	public static Multimap<TypeToken<?>, Binding<?>> indexTypeBindings(Collection<Binding<?>> bindings) {
		Multimap<TypeToken<?>, Binding<?>> typeBindings = ArrayListMultimap.create();
		
		for (Binding<?> binding : bindings) {
			BindingKey<?> key = binding.getBindingKey();
			
			typeBindings.put(key.getToken(), binding);
		}
		
		return typeBindings;
	}
	
}

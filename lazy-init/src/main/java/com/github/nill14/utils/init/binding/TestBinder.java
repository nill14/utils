package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Singleton;

import com.github.nill14.utils.annotation.Experimental;
import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.BindingBuilder;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.github.nill14.utils.init.binding.target.AnnotatedElementBindingTargetVisitor;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.ChainingPropertyResolver;
import com.github.nill14.utils.init.impl.SimplePropertyResolver;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.github.nill14.utils.init.scope.PrototypeScope;
import com.github.nill14.utils.init.scope.SingletonScope;
import com.github.nill14.utils.init.util.Element;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public final class TestBinder implements Binder {
	
	private final Map<Class<? extends Annotation>, IScope> scopes = new ConcurrentHashMap<>(); //does not allow nulls
	private final List<AbstractPropertyResolver> extraResolvers = Lists.newArrayList();
	private final ChainingPojoInitializer initializer = ChainingPojoInitializer.defaultInitializer();

	private final List<Element<BindingImpl<?>>> elements = Lists.newArrayList();
	private final AtomicBoolean configurationLocker = new AtomicBoolean(false);

	
	private Element<BindingImpl<?>> newElement() {
		Element<BindingImpl<?>> element = new Element<BindingImpl<?>>(configurationLocker);
		elements.add(element);
		return element;
	}
	
	@Override
	public <T> AnnotatedBindingBuilder<T> bind(TypeToken<T> typeToken) {
		return new BindingBuilder<T>(this, newElement(), this, typeToken);
	}

	@Override
	public <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
		return new BindingBuilder<T>(this, newElement(), this, TypeToken.of(type));
	}
	
	@Override
	public void bindScope(Class<? extends Annotation> annotationType, IScope scope) {
		scopes.put(annotationType, scope);
	}
	
	@Override
	public IScope getScope(Class<? extends Annotation> annotationType) {
		if (Singleton.class.equals(annotationType)) {
			return SingletonScope.instance();
		}
		IScope scope = scopes.get(annotationType);
		if (scope == null) {
			throw new RuntimeException("Scope is missing: " + annotationType);
		}
		return scope;
	}

	public TestBinder withInitializer(IPojoInitializer initializer) {
		this.initializer.append(initializer);
		return this;
	}
	
	/**
	 * 
	 * Example using MockitoFallBackResolver:
	 * 
	 * 
	 * <pre>
public Object resolve(Object pojo, IParameterType type) {
	return import org.mockito.Mockito.mock(type.getRawType(), Mockito.RETURNS_DEEP_STUBS);
}
	 * 
	 * </pre>
	 * 
	 */
	public TestBinder withFallbackResolver(AbstractPropertyResolver resolver) {
		this.extraResolvers.add(resolver);
		return this;
	}
	
	@Experimental
	@Deprecated
	public IPropertyResolver toResolver() {
		
		ImmutableList<BindingImpl<?>> bindings = freezeBindings();
		
		
		return new SimplePropertyResolver(bindings, 
				new ChainingPojoInitializer(initializer.getItems()));
	}
	
	
	public IBeanInjector toBeanInjector() {
//		bindScope(Singleton.class, SingletonScope.instance());
		
		ImmutableList<BindingImpl<?>> bindings = freezeBindings();
		
		
		
		if (extraResolvers.isEmpty()) {
			SimplePropertyResolver propertyResolver = new SimplePropertyResolver(bindings, 
					new ChainingPojoInitializer(initializer.getItems()));
			return propertyResolver.toBeanInjector();
		
		} else {
			ChainingPropertyResolver resolver = new ChainingPropertyResolver(extraResolvers, initializer);
			SimplePropertyResolver propertyResolver = new SimplePropertyResolver(bindings, resolver);
			resolver.insert(propertyResolver);
			return resolver.toBeanInjector();
		}
	}

	public ImmutableList<BindingImpl<?>> freezeBindings() {
		configurationLocker.set(true);
		
		ImmutableList<BindingImpl<?>> bindings = ImmutableList.copyOf(
				elements.stream().map(Element::getValue).map(this::scanQualifierAndScope).iterator());
		return bindings;
	}
	
	private <T> BindingImpl<T> scanQualifierAndScope(BindingImpl<T> binding) {
		AnnotatedElementBindingTargetVisitor targetVisitor = new AnnotatedElementBindingTargetVisitor();
		BindingKey<T> bindingKey = binding.getBindingKey();
		boolean isPrototypeScope = binding.getScope() == PrototypeScope.instance();
		//TODO make the prototype check safer
		
		if (bindingKey.getQualifier() == null || isPrototypeScope) {
			AnnotatedElement annotatedElement = binding.getBindingTarget().accept(targetVisitor);
			
			if (bindingKey.getQualifier() == null) {
				Annotation qualifier = AnnotationScanner.findQualifier(annotatedElement).orElse(null);
				binding = binding.keyWithQualifier(qualifier);
			}
			
			if (isPrototypeScope) {
				Annotation scopeAnnotation = AnnotationScanner.findScope(annotatedElement).orElse(null);
				if (scopeAnnotation != null) {
					IScope scope = getScope(scopeAnnotation.annotationType());
					binding = binding.withScope(scope);
				}
			}
		}
		
		return binding;
	}
	
}

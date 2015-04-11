package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.impl.BindingBuilder;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.github.nill14.utils.init.binding.target.LazyPojoBindingTargetVisitor;
import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.ChainingPropertyResolver;
import com.github.nill14.utils.init.impl.ServiceRegistry;
import com.github.nill14.utils.init.util.Element;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public final class TestBinder implements Binder {
	
	private final ServiceRegistry serviceRegistry = new ServiceRegistry();
	private final ChainingPojoInitializer initializer = ChainingPojoInitializer.defaultInitializer();
	private final ChainingPropertyResolver resolver = new ChainingPropertyResolver(serviceRegistry.toResolver());

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

	public TestBinder withResolver(IPropertyResolver resolver) {
		this.resolver.insert(resolver);
		return this;
	}
	
	public TestBinder withInitializer(IPojoInitializer initializer) {
		this.initializer.insert(initializer);
		return this;
	}
	
	/**
	 * 
	 * E.g. something like Mockito#mock(type#getRawType()) for tests when no binding is found
	 */
	public TestBinder withFallbackResolver(IPropertyResolver resolver) {
		this.resolver.append(resolver);
		return this;
	}
	
	
	public ServiceRegistry getRegistry() {
		return serviceRegistry;
	}
	
	public IBeanInjector toBeanInjector() {
		ImmutableList<BindingImpl<?>> bindings = freezeBindings();
		
		
		LazyPojoBindingTargetVisitor bindingTargetVisitor = new LazyPojoBindingTargetVisitor(
				resolver, initializer);
		
		
		for (BindingImpl<?> binding : bindings) {
			TypeToken<?> keyToken = binding.getKeyToken();
			Set<Annotation> qualifiers = binding.getQualifiers();
			
			ILazyPojo<?> lazyPojo = binding.getBindingTarget().accept(bindingTargetVisitor);
			
			serviceRegistry.addBinding(keyToken, qualifiers, lazyPojo);
		}
		
		return serviceRegistry.toBeanInjector();
	}

	public ImmutableList<BindingImpl<?>> freezeBindings() {
		configurationLocker.set(true);
		ImmutableList<BindingImpl<?>> bindings = ImmutableList.copyOf(
				elements.stream().map(Element::getValue).iterator());
		return bindings;
	}
	
}

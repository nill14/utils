package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.BindingBuilder;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.github.nill14.utils.init.binding.target.AnnotatedElementBindingTargetVisitor;
import com.github.nill14.utils.init.binding.target.LazyPojoBindingTargetVisitor;
import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.ChainingPropertyResolver;
import com.github.nill14.utils.init.impl.ServiceRegistry;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.github.nill14.utils.init.util.Element;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public final class ModuleBinder implements Binder {
	
	private final List<Element<BindingImpl<?>>> elements = Lists.newArrayList();
	private final ChainingPropertyResolver resolver;
	private final Object source;
	private final ServiceRegistry serviceRegistry;
	private final AtomicBoolean configurationLocker = new AtomicBoolean(false);

	public ModuleBinder(ServiceRegistry serviceRegistry, Object source) {
		this.serviceRegistry = serviceRegistry;
		this.source = source;
		resolver = new ChainingPropertyResolver(serviceRegistry.toResolver());
	}

	private Element<BindingImpl<?>> newElement() {
		Element<BindingImpl<?>> element = new Element<BindingImpl<?>>(configurationLocker);
		elements.add(element);
		return element;
	}
	
	@Override
	public <T> AnnotatedBindingBuilder<T> bind(TypeToken<T> typeToken) {
		return new BindingBuilder<T>(this, newElement(), source, typeToken);
	}

	@Override
	public <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
		return new BindingBuilder<T>(this, newElement(), source, TypeToken.of(type));
	}
	
	@Override
	public void bindScope(Class<? extends Annotation> annotationType, IScope scope) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	@Override
	public IScope getScope(Class<? extends Annotation> annotationType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public ModuleBinder withResolver(IPropertyResolver resolver) {
		this.resolver.insert(resolver);
		return this;
	}
	
	public ModuleBinder withInitializer(IPojoInitializer initializer) {
		this.resolver.appendInitializer(initializer);
		return this;
	}
	
	
	public void build() {
		configurationLocker.set(true);
		ImmutableList<BindingImpl<?>> bindings = ImmutableList.copyOf(
				elements.stream().map(Element::getValue).map(this::scanQualifier).iterator());
		
		
		LazyPojoBindingTargetVisitor bindingTargetVisitor = new LazyPojoBindingTargetVisitor(
				resolver, (binding) -> null); // we do not support linked bindings at the moment
		
		
		for (BindingImpl<?> binding : bindings) {
			BindingKey<?> BindingKey = binding.getBindingKey();
			
			ILazyPojo<?> lazyPojo = binding.getBindingTarget().accept(bindingTargetVisitor);
			
			serviceRegistry.addBinding(BindingKey, lazyPojo); //TODO add access check
		}
		
//		return serviceRegistry.toBeanInjector();
		
	}
	
	private <T> BindingImpl<T> scanQualifier(BindingImpl<T> binding) {
		AnnotatedElementBindingTargetVisitor targetVisitor = new AnnotatedElementBindingTargetVisitor();
		BindingKey<T> bindingKey = binding.getBindingKey();
		if (bindingKey.getQualifier() == null) {
			AnnotatedElement annotatedElement = binding.getBindingTarget().accept(targetVisitor);
			Annotation qualifier = AnnotationScanner.findQualifier(annotatedElement).orElse(null);
			return binding.keyWithQualifier(qualifier);
		}
		
		return binding;
	}
	
}

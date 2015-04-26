package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.nill14.utils.init.api.BindingType;
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
import com.google.inject.Scope;

public final class ModuleBinder implements Binder {
	
	private final List<Element<BindingImpl<?>>> elements = Lists.newArrayList();
	private final ChainingPojoInitializer initializer = ChainingPojoInitializer.defaultInitializer();
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
	public void bindScope(Class<? extends Annotation> annotationType, Scope scope) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public ModuleBinder withResolver(IPropertyResolver resolver) {
		this.resolver.insert(resolver);
		return this;
	}
	
	public ModuleBinder withInitializer(IPojoInitializer initializer) {
		this.initializer.insert(initializer);
		return this;
	}
	
	
	public void build() {
		configurationLocker.set(true);
		ImmutableList<BindingImpl<?>> bindings = ImmutableList.copyOf(
				elements.stream().map(Element::getValue).iterator());
		
		
		LazyPojoBindingTargetVisitor bindingTargetVisitor = new LazyPojoBindingTargetVisitor(
				resolver, initializer);
		
		
		for (BindingImpl<?> binding : bindings) {
			BindingType<?> bindingType = binding.getBindingType();
			
			ILazyPojo<?> lazyPojo = binding.getBindingTarget().accept(bindingTargetVisitor);
			
			serviceRegistry.addBinding(bindingType, lazyPojo); //TODO add access check
		}
		
//		return serviceRegistry.toBeanInjector();
		
	}
}

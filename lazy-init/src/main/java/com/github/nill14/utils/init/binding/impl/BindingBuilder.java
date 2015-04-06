package com.github.nill14.utils.init.binding.impl;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.inject.Provider;

import com.github.nill14.utils.init.binding.AnnotatedBindingBuilder;
import com.github.nill14.utils.init.binding.Binder;
import com.github.nill14.utils.init.binding.LinkedBindingBuilder;
import com.github.nill14.utils.init.binding.ScopedBindingBuilder;
import com.github.nill14.utils.init.binding.target.BeanInstanceBindingTarget;
import com.github.nill14.utils.init.binding.target.BeanTypeBindingTarget;
import com.github.nill14.utils.init.binding.target.ProviderInstanceBindingTarget;
import com.github.nill14.utils.init.binding.target.ProviderTypeBindingTarget;
import com.github.nill14.utils.init.meta.Annotations;
import com.github.nill14.utils.init.util.Element;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

public final class BindingBuilder<T> implements AnnotatedBindingBuilder<T> {
	
	private final TypeToken<T> keyToken;
	private Annotation annotation;
	private BindingTarget<? extends T> target;
	private final Object source;
	private final Element<BindingImpl<?>> element;

	
	public BindingBuilder(Binder binder, Element<BindingImpl<?>> element, Object source, TypeToken<T> bindToken) {
		this.element = element;
		this.source = source;
		this.keyToken = bindToken;
		this.target = new BeanTypeBindingTarget<>(bindToken);
		
		element.update(new BindingImpl<T>(bindToken, ImmutableSet.of(), target, source));
	}


	@Override
	public ScopedBindingBuilder to(Class<? extends T> implementation) {
		target = new BeanTypeBindingTarget<>(TypeToken.of(implementation));
		buildBinder();
		return this;
	}

	@Override
	public ScopedBindingBuilder to(TypeToken<? extends T> implementation) {
		target = new BeanTypeBindingTarget<>(implementation);
		buildBinder();
		return this;
	}

	@Override
	public void toInstance(T instance) {
		target = new BeanInstanceBindingTarget<>(instance);
		buildBinder();
	}

	@Override
	public ScopedBindingBuilder toProvider(Provider<? extends T> provider) {
		target = new ProviderInstanceBindingTarget<>(provider);
		buildBinder();
		return this;
	}

	@Override
	public ScopedBindingBuilder toProvider(Class<? extends Provider<? extends T>> providerType) {
		target = new ProviderTypeBindingTarget<>(TypeToken.of(providerType));
		buildBinder();
		return this;
	}

	@Override
	public ScopedBindingBuilder toProvider(TypeToken<? extends Provider<? extends T>> providerType) {
		target = new ProviderTypeBindingTarget<>(providerType);
		buildBinder();
		return this;
	}

	@Override
	public void in(Class<? extends Annotation> scopeAnnotation) {
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public LinkedBindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
		this.annotation = Annotations.annotation(annotationType);
		buildBinder();
		return this;
	}

	@Override
	public LinkedBindingBuilder<T> annotatedWith(Annotation annotation) {
		this.annotation = annotation;
		buildBinder();
		return this;
	}
	
	private void buildBinder() {
		Set<Annotation> annotations = annotation == null ? ImmutableSet.of() : ImmutableSet.of(annotation); 
		
		element.update(new BindingImpl<T>(keyToken, annotations, target, source));
	}

}

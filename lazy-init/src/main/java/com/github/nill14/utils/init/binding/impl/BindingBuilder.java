package com.github.nill14.utils.init.binding.impl;

import java.lang.annotation.Annotation;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.binding.AnnotatedBindingBuilder;
import com.github.nill14.utils.init.binding.Binder;
import com.github.nill14.utils.init.binding.LinkedBindingBuilder;
import com.github.nill14.utils.init.binding.ScopedBindingBuilder;
import com.github.nill14.utils.init.binding.target.BeanInstanceBindingTarget;
import com.github.nill14.utils.init.binding.target.BeanTypeBindingTarget;
import com.github.nill14.utils.init.binding.target.ProviderInstanceBindingTarget;
import com.github.nill14.utils.init.binding.target.ProviderTypeBindingTarget;
import com.github.nill14.utils.init.meta.Annotations;
import com.github.nill14.utils.init.scope.AnnotationScopeStrategy;
import com.github.nill14.utils.init.scope.IScopeStrategy;
import com.github.nill14.utils.init.util.Element;
import com.google.common.reflect.TypeToken;

public final class BindingBuilder<T> implements AnnotatedBindingBuilder<T> {
	
	private final TypeToken<T> keyToken;
	private Annotation qualifier;
	private BindingTarget<? extends T> target;
	private IScopeStrategy scope = AnnotationScopeStrategy.prototype();
	private final Object source;
	private final Element<Binding<?>> element;
	private final Binder binder;

	
	public BindingBuilder(Binder binder, Element<Binding<?>> element, Object source, TypeToken<T> bindToken) {
		this.binder = binder;
		this.element = element;
		this.source = source;
		this.keyToken = bindToken;
		this.target = new BeanTypeBindingTarget<>(bindToken);
		
		//no qualifier at the moment of creation
		element.update(new Binding<T>(BindingKey.of(bindToken), target, scope, source));
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
		scope = AnnotationScopeStrategy.of(scopeAnnotation);
		buildBinder();
	}

	@Override
	public LinkedBindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
		this.qualifier = Annotations.annotation(annotationType);
		buildBinder();
		return this;
	}

	@Override
	public LinkedBindingBuilder<T> annotatedWith(Annotation annotation) {
		this.qualifier = annotation;
		buildBinder();
		return this;
	}
	
	private void buildBinder() {
		BindingKey<T> bindingKey = BindingKey.of(keyToken, qualifier); 
		
		element.update(new Binding<T>(bindingKey, target, scope, source));
	}

}

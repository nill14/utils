package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.Binding;
import com.github.nill14.utils.init.binding.impl.BindingBuilder;
import com.github.nill14.utils.init.binding.target.AnnotatedElementBindingTargetVisitor;
import com.github.nill14.utils.init.impl.BinderUtils;
import com.github.nill14.utils.init.impl.CallerContext;
import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.ChainingPropertyResolver;
import com.github.nill14.utils.init.impl.SimplePropertyResolver;
import com.github.nill14.utils.init.meta.AnnotationScanner;
import com.github.nill14.utils.init.scope.ScopeStrategies;
import com.github.nill14.utils.init.util.Element;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public final class ModuleBinder implements Binder {
	
	private final Map<Class<? extends Annotation>, IScope> scopes = Maps.newHashMap();
	private final ChainingPropertyResolver resolver = new ChainingPropertyResolver();

	private final List<Element<Binding<?>>> elements = Lists.newArrayList();
	private final AtomicBoolean configurationLocker = new AtomicBoolean(false);	
	
	private final Object source;
	private final Binder parent;

	public ModuleBinder(Binder parent, Object source) {
		this.parent = parent;
		this.source = source;
	}

	private Element<Binding<?>> newElement() {
		Element<Binding<?>> element = new Element<Binding<?>>(configurationLocker);
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
		scopes.put(annotationType, scope); //TODO IScopeStrategy?
	}
	
	public IPropertyResolver toResolver() {
		
		ImmutableList<Binding<?>> bindings = freezeBindings();
		
		
		return new SimplePropertyResolver(bindings, 
				new ChainingPojoInitializer(resolver.getInitializers()));
	}
	
	public ImmutableList<Binding<?>> freezeBindings() {
		configurationLocker.set(true);
		
		ImmutableList<Binding<?>> bindings = ImmutableList.copyOf(
				elements.stream().map(Element::getValue).map(BinderUtils::scanQualifierAndScope).map(binding -> ScopeStrategies.replaceScopes(binding, scopes)).iterator());
		return bindings;
	}	
	
	public IBeanInjector toBeanInjector() {
//		bindScope(Singleton.class, SingletonScope.instance());
		
		ImmutableList<Binding<?>> bindings = freezeBindings();
		
		
		SimplePropertyResolver propertyResolver = new SimplePropertyResolver(bindings, 
				new ChainingPojoInitializer(resolver.getInitializers()));
		
		
		
		return propertyResolver.toBeanInjector(CallerContext.prototype());
	}
	
	public void build() {
		toBeanInjector();
		
//		return serviceRegistry.toBeanInjector();
		
	}
	
	private <T> Binding<T> scanQualifier(Binding<T> binding) {
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

package com.github.nill14.utils.init.binding;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.nill14.utils.annotation.Experimental;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.Binding;
import com.github.nill14.utils.init.binding.impl.BindingBuilder;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
import com.github.nill14.utils.init.impl.BinderUtils;
import com.github.nill14.utils.init.impl.CallerContext;
import com.github.nill14.utils.init.impl.ChainingPojoInitializer;
import com.github.nill14.utils.init.impl.ChainingPropertyResolver;
import com.github.nill14.utils.init.impl.SimplePropertyResolver;
import com.github.nill14.utils.init.inject.ReflectionUtils;
import com.github.nill14.utils.init.scope.ScopeStrategies;
import com.github.nill14.utils.init.util.Element;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public final class TestBinder implements Binder {
	
	private final Map<Class<? extends Annotation>, IScope> scopes = new ConcurrentHashMap<>(); //does not allow nulls
	private final List<AbstractPropertyResolver> extraResolvers = Lists.newArrayList();
	private final ChainingPojoInitializer initializer = ChainingPojoInitializer.defaultInitializer();

	private final List<Element<Binding<?>>> elements = Lists.newArrayList();
	private final AtomicBoolean configurationLocker = new AtomicBoolean(false);

	
	private Element<Binding<?>> newElement() {
		Element<Binding<?>> element = new Element<Binding<?>>(configurationLocker);
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
		
		ImmutableList<Binding<?>> bindings = freezeBindings();
		
		
		return new SimplePropertyResolver(bindings, 
				new ChainingPojoInitializer(initializer.getItems()));
	}
	
	
	public IBeanInjector toBeanInjector() {
		ImmutableList<Binding<?>> bindings = freezeBindings();
		
		
		
		if (extraResolvers.isEmpty()) {
			SimplePropertyResolver propertyResolver = new SimplePropertyResolver(bindings, 
					new ChainingPojoInitializer(initializer.getItems()));
			return propertyResolver.toBeanInjector(CallerContext.prototype());
		
		} else {
			ChainingPropertyResolver resolver = new ChainingPropertyResolver(extraResolvers, initializer);
			SimplePropertyResolver propertyResolver = new SimplePropertyResolver(bindings, resolver);
			resolver.insert(propertyResolver);
			return resolver.toBeanInjector(CallerContext.prototype());
		}
	}

	public ImmutableList<Binding<?>> freezeBindings() {
		configurationLocker.set(true);
		
		ImmutableList<Binding<?>> bindings = ImmutableList.copyOf(
				elements.stream().map(Element::getValue).map(BinderUtils::scanQualifierAndScope).map(binding -> ScopeStrategies.replaceScopes(binding, scopes)).iterator());
		return bindings;
	}
	

	
	public void scanProvidesBindings(Object module) {
		List<Binding<?>> list = ReflectionUtils.scanProvidesBindings(this, module);
		for (final Binding<?> binding : list) {
			final Element<Binding<?>> element = newElement();
			element.update(binding);
		}
	}
}

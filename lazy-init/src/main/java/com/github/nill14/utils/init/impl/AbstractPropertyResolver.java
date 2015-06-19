package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.ICallerContext;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IQualifiedProvider;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

@SuppressWarnings("serial")
public abstract class AbstractPropertyResolver implements IPropertyResolver {
	
	private transient volatile IBeanInjector beanInjector;
	private final ChainingPojoInitializer initializer;
	protected final IPropertyResolver resolver;
	
	public AbstractPropertyResolver() {
		initializer = ChainingPojoInitializer.defaultInitializer();
		resolver = this; 
	}
	
	public AbstractPropertyResolver(ChainingPojoInitializer initializer) {
		this.initializer = initializer;
		this.resolver = this;
	}
	
	public AbstractPropertyResolver(ChainingPropertyResolver parent) {
		this.resolver = parent;
		this.initializer = parent.getInitializer();
	}	
	
	@Override
	public Object resolve(IParameterType type, ICallerContext context) {
		
		boolean isCollection = type.isCollection();
		if (isCollection || type.isOptional()) { 
			Class<?> baseType = type.getRawType();
			IParameterType paramType = type.getFirstParamType();

			if (java.util.Optional.class.isAssignableFrom(baseType)) {
				return java.util.Optional.ofNullable(doResolve(paramType, context));
			}
			
			if (com.google.common.base.Optional.class.isAssignableFrom(baseType)) {
				return com.google.common.base.Optional.fromNullable(doResolve(paramType, context));
			}
			
			if (Iterable.class.isAssignableFrom(baseType)) {
				return doResolveCollection(baseType, paramType, context);
			}
		} 
	
		Object result = doResolve(type, context);
		if (result != null) {
			return result;
		}

		return doPrototype(type, context);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object doResolve(IParameterType type, ICallerContext context) {
		Class<?> rawType = type.getRawType();
		
		if (IBeanInjector.class.equals(rawType)) {
			return toBeanInjector(context);
		
		} else if (IQualifiedProvider.class.equals(rawType)) {
			return new QualifiedProvider(type.getFirstParamToken(), resolver);
		
		} else if (Provider.class.equals(rawType)) {
			return new LazyResolvingProvider<>(resolver, type.getFirstParamType(), context);
		}
		
		Optional<String> named = type.getNamed();
		Annotation qualifier = type.getQualifier();
		if (qualifier != null) {
			Object result = findByQualifier(type, qualifier, context);
			if (result == null && named.isPresent()) {
				return findByName(named.get(), type, context);
			}
			return result;
		
		} else if (named.isPresent()) { // find by name if supported
			return findByName(named.get(), type, context);

		} else { 
			// find by type
			Object result = findByType(type, context);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	protected Object doResolveCollection(Class<?> collectionType, IParameterType paramType, ICallerContext context) {
		Collection<?> providers = findAllByType(paramType, context);
		Preconditions.checkNotNull(providers);
		
		if (collectionType.isAssignableFrom(ImmutableList.class)) {
			return ImmutableList.copyOf(providers);
		
		} else if (collectionType.isAssignableFrom(ImmutableSet.class)) {
			return ImmutableSet.copyOf(providers);
		
		} else if (collectionType.isAssignableFrom(ImmutableSortedSet.class)) {
			return ImmutableSortedSet.copyOf(providers);
		
		} else {
			throw new RuntimeException(collectionType + "is an unsupported collection type");
		}
	}
	
	
	protected abstract @Nullable Object findByName(String name, IParameterType type, ICallerContext context);
	protected abstract @Nullable Object findByType(IParameterType type, ICallerContext context);
	protected abstract Collection<?> findAllByType(IParameterType type, ICallerContext context);

	protected abstract @Nullable Object findByQualifier(IParameterType type, Annotation qualifier, ICallerContext context);
	
	protected Object doPrototype(IParameterType type, ICallerContext context) {
		IBeanDescriptor<Object> typeDescriptor = new PojoInjectionDescriptor<>(type);
		if (typeDescriptor.canBeInstantiated()) {
			return new BeanTypePojoFactory<>(typeDescriptor).newInstance(resolver, context);
		}
		return null;
	}
	
	@Override
	public IBeanInjector toBeanInjector(ICallerContext context) {
		IBeanInjector beanInjector = this.beanInjector;
		if (beanInjector == null) {
			this.beanInjector = beanInjector = new BeanInjector(resolver, context);
		}
		return beanInjector;
	}
	
	
	@Override
	public <T> void initializeBean(IBeanDescriptor<T> beanDescriptor, Object instance, ICallerContext context) {
		initializer.init(resolver, beanDescriptor, instance, context);
	}

	@Override
	public <T> void destroyBean(IBeanDescriptor<T> beanDescriptor, Object instance) {
		initializer.destroy(resolver, beanDescriptor, instance);
	}
	
	@Override
	public void insertInitializer(IPojoInitializer initializer) {
		this.initializer.insert(initializer);
	}
	
	@Override
	public void appendInitializer(IPojoInitializer initializer) {
		this.initializer.append(initializer);
	}
	
	
	@Override
	public List<IPojoInitializer> getInitializers() {
		return initializer.getItems();
	}
	
	protected ChainingPojoInitializer getInitializer() {
		return initializer;
	}
	
}

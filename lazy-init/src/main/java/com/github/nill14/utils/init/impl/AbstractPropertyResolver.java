package com.github.nill14.utils.init.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
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
	private final ChainingPojoInitializer initializer = new ChainingPojoInitializer();
	
	public AbstractPropertyResolver() {
	}
	
	public AbstractPropertyResolver(IPojoInitializer initializer) {
		this.initializer.insert(initializer);
	}
	
	@Override
	public Object resolve(IParameterType type) {
		
		boolean isCollection = isCollection(type);
		if (isCollection || type.isOptional()) { 
			Class<?> baseType = type.getRawType();
			IParameterType paramType = type.getFirstParamType();

			if (java.util.Optional.class.isAssignableFrom(baseType)) {
				return java.util.Optional.ofNullable(doResolve(paramType));
			}
			
			if (com.google.common.base.Optional.class.isAssignableFrom(baseType)) {
				return com.google.common.base.Optional.fromNullable(doResolve(paramType));
			}
			
			if (Iterable.class.isAssignableFrom(baseType)) {
				return doResolveCollection(baseType, paramType);
			}
		} 
	
		Object result = doResolve(type);
		if (result != null) {
			return result;
		}

		return doPrototype(type);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object doResolve(IParameterType type) {
		Class<?> rawType = type.getRawType();
		
		if (IBeanInjector.class.equals(rawType)) {
			return toBeanInjector();
		
		} else if (IQualifiedProvider.class.equals(rawType)) {
			return new QualifiedProvider(type.getFirstParamToken(), this);
		
		} else if (Provider.class.equals(rawType)) {
			return new LazyResolvingProvider<>(this, type.getFirstParamType());
		}
		
		Optional<String> named = type.getNamed();
		Annotation qualifier = type.getQualifier();
		if (qualifier != null) {
			Object result = findByQualifier(type, qualifier);
			if (result == null && named.isPresent()) {
				return findByName(named.get(), type);
			}
			return result;
		
		} else if (named.isPresent()) { // find by name if supported
			return findByName(named.get(), type);

		} else { 
			// find by type
			Object result = findByType(type);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	protected Object doResolveCollection(Class<?> collectionType, IParameterType paramType) {
		Collection<?> providers = findAllByType(paramType);
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
	
	
	protected abstract @Nullable Object findByName(String name, IParameterType type);
	protected abstract @Nullable Object findByType(IParameterType type);
	protected abstract Collection<?> findAllByType(IParameterType type);

	protected abstract @Nullable Object findByQualifier(IParameterType type, Annotation qualifier);
	
	protected boolean isCollection(IParameterType type) {
		return Iterable.class.isAssignableFrom(type.getRawType());
	}
	
	protected Object doPrototype(IParameterType type) {
		IBeanDescriptor<Object> typeDescriptor = new PojoInjectionDescriptor<>(type);
		if (typeDescriptor.canBeInstantiated()) {
			return new BeanTypePojoFactory<>(typeDescriptor).newInstance(this);
		}
		return null;
	}
	
	@Override
	public IBeanInjector toBeanInjector() {
		IBeanInjector beanInjector = this.beanInjector;
		if (beanInjector == null) {
			this.beanInjector = beanInjector = new BeanInjector(this);
		}
		return beanInjector;
	}
	
	
	@Override
	public <T> void initializeBean(IBeanDescriptor<T> beanDescriptor, Object instance) {
		initializer.init(this, beanDescriptor, instance);
	}

	@Override
	public <T> void destroyBean(IBeanDescriptor<T> beanDescriptor, Object instance) {
		initializer.destroy(this, beanDescriptor, instance);
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
}

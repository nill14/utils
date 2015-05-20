package com.github.nill14.utils.init.integration;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.impl.PojoFactoryAdapter;
import com.github.nill14.utils.init.impl.PojoInjectionFactory;
import com.google.common.reflect.TypeToken;

public /*non-final on purpose*/ class LazyPojoFactory<F> implements IPojoFactory<F> {


	public static <T> LazyPojoFactory<T> forClass(Class<T> beanClass) {
		return new LazyPojoFactory<>(TypeToken.of(beanClass));
	}

	public static <T, F extends Provider<T>> LazyPojoFactory<T> forFactory(Class<F> factoryType) {
		return new LazyPojoFactory<>(TypeToken.of(factoryType), factoryType);
	}

	
	private static final long serialVersionUID = 1L;
	private IPropertyResolver resolver;
	private IPojoInitializer initializer;
	
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	
	private final boolean doubleFactory;
	private final IPojoFactory<F> delegate;
	private final TypeToken<F> factoryToken;


	protected LazyPojoFactory(TypeToken<F> beanType) {
		delegate = new PojoInjectionFactory<>(beanType, delegatingResolver);
		factoryToken = beanType;
		this.doubleFactory = false;
	}

	//class type F is here G - factory
	@SuppressWarnings("unchecked")
	protected <T, G extends Provider<T>> LazyPojoFactory(TypeToken<G> factoryType, Class<G> factoryClass) {
		PojoFactoryAdapter<T, G> factoryAdapter = new PojoFactoryAdapter<T, G>(factoryType, delegatingResolver);
		this.factoryToken = (TypeToken<F>) factoryType;
		this.delegate = (IPojoFactory<F>) factoryAdapter;
		this.doubleFactory = true;
	}
	
	public boolean isDoubleFactory() {
		return doubleFactory;
	}

	@Override
	public F newInstance() {
		F instance = delegate.newInstance();
		resolver.initializeBean(instance);
		return instance;
	}

	@Override
	public TypeToken<F> getType() {
		return factoryToken;
	}

	@Override
	public IBeanDescriptor<F> getDescriptor() {
		return delegate.getDescriptor();
	}

	@Override
	public IPropertyResolver getResolver() {
		try {
			lock.readLock().lock();
			return resolver;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void setResolver(IPropertyResolver resolver) {
		try {
			lock.writeLock().lock();
			this.resolver = resolver;
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void setInitializer(IPojoInitializer initializer) {
		try {
			lock.writeLock().lock();
			this.initializer = initializer;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public IPojoInitializer getInitializer() {
		try {
			lock.readLock().lock();
			return initializer;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	private final IPojoInitializer delegatingInitializer = new IPojoInitializer() {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void init(IPojoFactory<?> pojoFactory, Object instance) {
			getInitializer().init(pojoFactory, instance);
		}
		
		@Override
		public void destroy(IPojoFactory<?> pojoFactory, Object instance) {
			getInitializer().destroy(pojoFactory, instance);
		}
	};
	
	private final IPropertyResolver delegatingResolver = new IPropertyResolver() {
		
		private static final long serialVersionUID = 1L;
		
		@Override
		public Object resolve(IParameterType type) {
			return getResolver().resolve(type);
		}

		@Override
		public IBeanInjector toBeanInjector() {
			return getResolver().toBeanInjector();
		}

		@Override
		public void initializeBean(Object instance) {
			getResolver().initializeBean(instance);
		}
		@Override
		public List<IPojoInitializer> getInitializers() {
			return getResolver().getInitializers();
		}
	};
}

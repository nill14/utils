package com.github.nill14.utils.init.scope;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.target.InitializingProvider;
import com.google.common.collect.Lists;

public final class ScopeContext {

	private final AtomicBoolean lifecycleTracker = new AtomicBoolean(true);
	private final ConcurrentHashMap<BindingKey<?>, ScopeProviderProxy<?>> map = new ConcurrentHashMap<>();
	
	public ScopeContext() {
	}

	@SuppressWarnings("unchecked")
	public <T> Provider<T> scope(BindingKey<T> type, Provider<T> unscoped) {
		if (!(unscoped instanceof InitializingProvider)) {
			throw new IllegalArgumentException();
		}
		
		if (lifecycleTracker.get()) {
			return (Provider<T>) map.computeIfAbsent(type, (t) -> new ScopeProviderProxy<>(
					(InitializingProvider<T>) unscoped));
		
		} else {
			throw new RuntimeException("Cannot retrieve a bean from terminated scope");
		}
	}
	
	
	

	private class ScopeProviderProxy<T> implements Provider<T> {
		private final InitializingProvider<T> unscoped;
		private volatile T instance;

		public ScopeProviderProxy(InitializingProvider<T> unscoped) {
			this.unscoped = unscoped;
		}

		@Override
		public T get() {
			T instance = this.instance;
			if (instance == null) {
				
				synchronized (this) {
					instance = this.instance;
					if (instance == null) {
						
						instance = unscoped.get();
						this.instance = instance;
					}
				}
			}

			return instance;
		}
		
		private void destroy() {
			T instance;
			
			synchronized (this) {
				instance = this.instance;
				this.instance = null;
			}
			
			if (instance != null) {
				IBeanDescriptor<T> beanDescriptor = unscoped.getDescriptor();
				IPropertyResolver resolver = unscoped.getResolver();
				resolver.destroyBean(beanDescriptor, instance);
			}
		}
	}
	
	public void terminate() {
		if (lifecycleTracker.compareAndSet(true, false)) {
			List<ScopeProviderProxy<?>> providers = Lists.newArrayList(map.values());
			map.clear();
			for (ScopeProviderProxy<?> provider : providers) {
				provider.destroy();
			}
		}
	}
	
}

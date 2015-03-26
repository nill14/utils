package com.github.nill14.utils.init.impl;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;
import com.google.common.reflect.TypeToken;

public class PojoFactoryAdapter<T, F extends Provider<? extends T>> implements IPojoFactory<T>, IPojoInitializer {
	
	private static final long serialVersionUID = 1L;
	private final ILazyPojo<F> lazyFactory;
	private final TypeToken<T> typeToken;
	private final IPojoFactory<F> pojoFactory;
	private final IPojoInitializer pojoInitializer;

    /** Cache the beanDescriptor */
    private IBeanDescriptor<T> beanDescriptor; 
    
	public PojoFactoryAdapter(IPojoFactory<F> pojoFactory, TypeToken<T> typeToken, IPojoInitializer factoryInitializer) {
		this.pojoFactory = pojoFactory;
		this.typeToken = typeToken;
		this.pojoInitializer = factoryInitializer;
		this.lazyFactory = new LazyPojo<F>(pojoFactory, factoryInitializer);
	}

	@Override
	public T newInstance() {
		return lazyFactory.getInstance().get();
	}

	@Override
	public TypeToken<T> getType() {
		return typeToken;
	}
	
	@Override
	public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
		//factoryInitializer was already invoked by lazyFactory.getInstance() call
		//now we inject the object returned from lazyFactory.getInstance().get()
		//since we are both the factory and initializer we get the chance to do init as well as
		// we want to inject also pojo properties (if any), not only factory properties
		pojoInitializer.init(lazyPojo, pojoFactory, instance);
	}
	
	@Override
	public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
		//delegate the destruction to the factoryInitializer
		//the factory is not re-used but re-created for each object
		lazyFactory.freeInstance();

		//cleanup on the pojo 
		pojoInitializer.destroy(lazyPojo, pojoFactory, instance);
	}
	
	@Override
	public IPropertyResolver getResolver() {
		return pojoFactory.getResolver();
	}
	
	@Override
	public IBeanDescriptor<T> getDescriptor() {
		//avoiding synchronization on purpose
		IBeanDescriptor<T> h = beanDescriptor;
		if (h == null) {
			h = new PojoInjectionDescriptor<>(typeToken);
			beanDescriptor = h;
		}
		return h;
	}

}
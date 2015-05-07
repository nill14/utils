package com.github.nill14.utils.init.binding.target;

import java.util.function.Function;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.github.nill14.utils.init.impl.LazyPojo;
import com.github.nill14.utils.init.impl.PojoFactoryAdapter;
import com.github.nill14.utils.init.impl.PojoInjectionFactory;
import com.github.nill14.utils.init.impl.PojoProviderFactory;

public class LazyPojoBindingTargetVisitor implements BindingTargetVisitor<ILazyPojo<?>> {

	
	private final IPropertyResolver resolver;
	private final IPojoInitializer initializer;
	private Function<BindingKey<?>, ILazyPojo<?>> lookupFunction;

	/**
	 * 
	 * @param resolver
	 * @param initializer
	 * @param lookupFunction A lookup function, may return null
	 */
	public LazyPojoBindingTargetVisitor(IPropertyResolver resolver, 
			IPojoInitializer initializer,
			Function<BindingKey<?>, 
			ILazyPojo<?>> lookupFunction) {
		this.resolver = resolver;
		this.initializer = initializer;
		this.lookupFunction = lookupFunction;
	}
	
	@Override
	public ILazyPojo<?> visit(BeanInstanceBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = PojoProviderFactory.singleton(bindingTarget.getInstance(), resolver);
		return LazyPojo.forFactory(pojoFactory, initializer);
	}

	@Override
	public ILazyPojo<?> visit(ProviderInstanceBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = new PojoProviderFactory<>(bindingTarget.getProvider(), resolver);
		return LazyPojo.forFactory(pojoFactory, initializer);
	}

	@Override
	public ILazyPojo<?> visit(BeanTypeBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = new PojoInjectionFactory<>(bindingTarget.getToken(), resolver);
		return LazyPojo.forFactory(pojoFactory, initializer);
	}

	@Override
	public ILazyPojo<?> visit(ProviderTypeBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = new PojoFactoryAdapter<>(bindingTarget.getToken(), resolver, initializer);
		return LazyPojo.forFactory(pojoFactory, initializer);
	}

	@Override
	public ILazyPojo<?> visit(ProvidesMethodBindingTarget<?> bindingTarget) {
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		IPojoFactory<?> pojoFactory = new PojoProviderFactory(bindingTarget.getToken(), new Provider<Object>() {
			@Override
			public Object get() {
				return bindingTarget.injectMethod(resolver);
			}
		}, resolver);
		return LazyPojo.forFactory(pojoFactory, initializer);
	}
	
	
	@Override
	public ILazyPojo<?> visit(LinkedBindingTarget<?> linkedBindingTarget) {
		BindingKey<?> BindingKey = linkedBindingTarget.getBindingType();
		ILazyPojo<?> lazyPojo = lookupFunction.apply(BindingKey);
		if (lazyPojo == null) {
			//linked binding was not found, thus create a new lazyPojo
			//the same flow as for BeanTypeBindingTarget
			IPojoFactory<?> pojoFactory = new PojoInjectionFactory<>(BindingKey.getToken(), resolver);
			return LazyPojo.forFactory(pojoFactory, initializer);
		}

		return lazyPojo;
	}

}

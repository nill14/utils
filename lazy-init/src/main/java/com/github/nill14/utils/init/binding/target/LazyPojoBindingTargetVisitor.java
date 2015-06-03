package com.github.nill14.utils.init.binding.target;

import java.util.function.Function;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.github.nill14.utils.init.impl.BeanInstancePojoFactory;
import com.github.nill14.utils.init.impl.BeanTypePojoFactory;
import com.github.nill14.utils.init.impl.LazyPojo;
import com.github.nill14.utils.init.impl.ProviderTypePojoFactory;
import com.github.nill14.utils.init.impl.ProviderInstancePojoFactory;
import com.google.common.reflect.TypeToken;

@Deprecated
public class LazyPojoBindingTargetVisitor implements BindingTargetVisitor<ILazyPojo<?>> {

	
	private final IPropertyResolver resolver;
	private final Function<BindingKey<?>, ILazyPojo<?>> lookupFunction;

	/**
	 * 
	 * @param resolver
	 * @param initializer
	 * @param lookupFunction A lookup function, may return null
	 */
	public LazyPojoBindingTargetVisitor(IPropertyResolver resolver, 
			Function<BindingKey<?>, 
			ILazyPojo<?>> lookupFunction) {
		this.resolver = resolver;
		this.lookupFunction = lookupFunction;
	}
	
	@Override
	public ILazyPojo<?> visit(BeanInstanceBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = BeanInstancePojoFactory.singleton(bindingTarget.getInstance());
		return LazyPojo.forFactory(pojoFactory, resolver);
	}

	@Override
	public ILazyPojo<?> visit(ProviderInstanceBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = new ProviderInstancePojoFactory<Object>(
				(TypeToken<Object>) bindingTarget.getProviderToken(), 
				(Provider<Object>) bindingTarget.getProvider());
		return LazyPojo.forFactory(pojoFactory, resolver);
	}

	@Override
	public ILazyPojo<?> visit(BeanTypeBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = new BeanTypePojoFactory<>(bindingTarget.getToken());
		return LazyPojo.forFactory(pojoFactory, resolver);
	}

	@Override
	public ILazyPojo<?> visit(ProviderTypeBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = new ProviderTypePojoFactory<>(bindingTarget.getToken());
		return LazyPojo.forFactory(pojoFactory, resolver);
	}

	@Override
	public ILazyPojo<?> visit(ProvidesMethodBindingTarget<?> bindingTarget) {
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		IPojoFactory<?> pojoFactory = new ProviderInstancePojoFactory(bindingTarget.getToken(), new Provider<Object>() {
			@Override
			public Object get() {
				return bindingTarget.injectMethod(resolver);
			}
		});
		return LazyPojo.forFactory(pojoFactory, resolver);
	}
	
	
	@Override
	public ILazyPojo<?> visit(LinkedBindingTarget<?> linkedBindingTarget) {
		BindingKey<?> bindingKey = linkedBindingTarget.getBindingKey();
		ILazyPojo<?> lazyPojo = lookupFunction.apply(bindingKey);
		if (lazyPojo == null) {
			//linked binding was not found, thus create a new lazyPojo
			//the same flow as for BeanTypeBindingTarget
			IPojoFactory<?> pojoFactory = new BeanTypePojoFactory<>(bindingKey.getToken());
			return LazyPojo.forFactory(pojoFactory, resolver);
		}

		return lazyPojo;
	}

}

package com.github.nill14.utils.init.binding.target;

import java.util.function.Function;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.github.nill14.utils.init.impl.BeanInstancePojoFactory;
import com.github.nill14.utils.init.impl.BeanTypePojoFactory;
import com.github.nill14.utils.init.impl.ProviderTypePojoFactory;
import com.github.nill14.utils.init.impl.ProviderInstancePojoFactory;
import com.google.common.reflect.TypeToken;

public class PojoFactoryBindingTargetVisitor implements BindingTargetVisitor<IPojoFactory<?>> {

	
	private final IPropertyResolver resolver;
	private final Function<BindingKey<?>, IPojoFactory<?>> lookupFunction;

	/**
	 * 
	 * @param resolver
	 * @param initializer
	 * @param lookupFunction A lookup function, may return null
	 */
	public PojoFactoryBindingTargetVisitor(IPropertyResolver resolver, 
			Function<BindingKey<?>, 
			IPojoFactory<?>> lookupFunction) {
		this.resolver = resolver;
		this.lookupFunction = lookupFunction;
	}
	
	@Override
	public IPojoFactory<?> visit(BeanInstanceBindingTarget<?> bindingTarget) {
		return BeanInstancePojoFactory.singleton(bindingTarget.getInstance());
	}

	@SuppressWarnings("unchecked")
	@Override
	public IPojoFactory<?> visit(ProviderInstanceBindingTarget<?> bindingTarget) {
		return ProviderInstancePojoFactory.of((TypeToken<Object>) bindingTarget.getProviderToken(), (Provider<Object>) bindingTarget.getProvider());
	}

	@Override
	public IPojoFactory<?> visit(BeanTypeBindingTarget<?> bindingTarget) {
		return new BeanTypePojoFactory<>(bindingTarget.getToken());
	}

	@Override
	public IPojoFactory<?> visit(ProviderTypeBindingTarget<?> bindingTarget) {
		return new ProviderTypePojoFactory<>(bindingTarget.getToken());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IPojoFactory<?> visit(ProvidesMethodBindingTarget<?> bindingTarget) {
		return ProviderInstancePojoFactory.of((TypeToken<Object>) bindingTarget.getToken(), 
				() -> bindingTarget.injectMethod(resolver));
	}
	
	
	@Override
	public IPojoFactory<?> visit(LinkedBindingTarget<?> linkedBindingTarget) {
		BindingKey<?> bindingKey = linkedBindingTarget.getBindingKey();
		IPojoFactory<?> pojoFactory = lookupFunction.apply(bindingKey);
		if (pojoFactory == null) {
			//linked binding was not found, thus create a new lazyPojo
			//the same flow as for BeanTypeBindingTarget
			return new BeanTypePojoFactory<>(bindingKey.getToken());
		}

		return pojoFactory; //ehm: TODO caching results
	}

}

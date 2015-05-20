package com.github.nill14.utils.init.binding.target;

import java.util.function.Function;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.github.nill14.utils.init.impl.PojoFactoryAdapter;
import com.github.nill14.utils.init.impl.PojoInjectionFactory;
import com.github.nill14.utils.init.impl.PojoProviderFactory;
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
		return PojoProviderFactory.singleton(bindingTarget.getInstance(), resolver);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IPojoFactory<?> visit(ProviderInstanceBindingTarget<?> bindingTarget) {
		return PojoProviderFactory.of((TypeToken<Object>) bindingTarget.getProviderToken(), (Provider<Object>) bindingTarget.getProvider(), resolver);
	}

	@Override
	public IPojoFactory<?> visit(BeanTypeBindingTarget<?> bindingTarget) {
		return new PojoInjectionFactory<>(bindingTarget.getToken(), resolver);
	}

	@Override
	public IPojoFactory<?> visit(ProviderTypeBindingTarget<?> bindingTarget) {
		return new PojoFactoryAdapter<>(bindingTarget.getToken(), resolver);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IPojoFactory<?> visit(ProvidesMethodBindingTarget<?> bindingTarget) {
		return PojoProviderFactory.of((TypeToken<Object>) bindingTarget.getToken(), 
				() -> bindingTarget.injectMethod(resolver), resolver);
	}
	
	
	@Override
	public IPojoFactory<?> visit(LinkedBindingTarget<?> linkedBindingTarget) {
		BindingKey<?> bindingKey = linkedBindingTarget.getBindingKey();
		IPojoFactory<?> pojoFactory = lookupFunction.apply(bindingKey);
		if (pojoFactory == null) {
			//linked binding was not found, thus create a new lazyPojo
			//the same flow as for BeanTypeBindingTarget
			return new PojoInjectionFactory<>(bindingKey.getToken(), resolver);
		}

		return pojoFactory; //ehm: TODO caching results
	}

}

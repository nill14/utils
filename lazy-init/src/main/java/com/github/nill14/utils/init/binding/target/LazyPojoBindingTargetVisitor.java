package com.github.nill14.utils.init.binding.target;

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

	public LazyPojoBindingTargetVisitor(IPropertyResolver resolver, IPojoInitializer initializer) {
		this.resolver = resolver;
		this.initializer = initializer;
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
	public ILazyPojo<?> visit(ProvidesMethodBindingTarget<?> providesMethodBindingTarget) {
		throw new UnsupportedOperationException();
	}

}

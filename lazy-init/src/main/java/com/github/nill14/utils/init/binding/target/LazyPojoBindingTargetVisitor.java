package com.github.nill14.utils.init.binding.target;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.impl.BindingTargetVisitor;
import com.github.nill14.utils.init.impl.BeanInstancePojoFactory;
import com.github.nill14.utils.init.impl.BeanTypePojoFactory;
import com.github.nill14.utils.init.impl.LazyPojo;
import com.github.nill14.utils.init.impl.MethodPojoFactory;
import com.github.nill14.utils.init.impl.ProviderInstancePojoFactory;
import com.github.nill14.utils.init.impl.ProviderTypePojoFactory;
import com.google.common.reflect.TypeToken;

@Deprecated
public class LazyPojoBindingTargetVisitor implements BindingTargetVisitor<ILazyPojo<?>> {

	
	private final IPropertyResolver resolver;

	/**
	 * 
	 * @param resolver
	 */
	public LazyPojoBindingTargetVisitor(IPropertyResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public ILazyPojo<?> visit(BeanInstanceBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = BeanInstancePojoFactory.singleton(bindingTarget.getInstance());
		return LazyPojo.forFactory(pojoFactory, resolver);
	}

	@Override
	public ILazyPojo<?> visit(ProviderInstanceBindingTarget<?> bindingTarget) {
		IPojoFactory<?> pojoFactory = new ProviderInstancePojoFactory<Object>(
				(TypeToken<Object>) bindingTarget.getToken(), 
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
		
		IPojoFactory<?> pojoFactory = MethodPojoFactory.of(bindingTarget.getToken(), 
				bindingTarget.getMethod(), bindingTarget.getInstance());
		return LazyPojo.forFactory(pojoFactory, resolver);
	}
	
	
	@Override
	public ILazyPojo<?> visit(LinkedBindingTarget<?> linkedBindingTarget) {
		throw new IllegalStateException("Should not get here");
	}

}

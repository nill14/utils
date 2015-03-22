package com.github.nill14.utils.moduledi.guice;

import java.util.Collection;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceServiceBuilder extends AbstractModule implements IServiceBuilder {

	@Override
	public IServiceBuilder registerServices(IServiceRegistry registry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S, T extends S> IServiceBuilder addBean(Class<T> impl, Class<S> iface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IServiceBuilder buildDependencies(IDependencyDescriptorBuilder<Class<?>> dependencyBuilder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<?>> getBeans() {
	    Injector injector = Guice.createInjector(this);

//	    injector.getAllBindings().values().stream().map(b -> b.)
//	    ISnackService snackService = injector.getInstance(ISnackService.class);
		return null;
	}

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
	  bind(IBeanInjector.class).to(GuiceBeanInjector.class);
//		    bind(CreditCardProcessor.class).to(PaypalCreditCardProcessor.class);
//		    bind(BillingService.class).to(RealBillingService.class);
	}

}

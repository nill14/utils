package com.github.nill14.utils.moduledi.guice;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Provider;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.LazyPojo;
import com.github.nill14.utils.init.meta.Annotations;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class GuiceServiceRegistry implements IServiceRegistry {

	private final Injector injector;
	private final Binder b;

	public GuiceServiceRegistry() {
		ModuleInternal module = new ModuleInternal();
		injector = Guice.createInjector(module);
		b = module.binder;
		try {
			Field field = b.getClass().getDeclaredField("moduleSource");
			field.setAccessible(true);
			field.set(b, module.moduleSource);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private final class ModuleInternal implements Module {

		private Binder binder;
		private Object moduleSource;

		@Override
		public void configure(Binder binder) {
			this.binder = binder;
			try {
				Field field = binder.getClass().getDeclaredField("moduleSource");
				field.setAccessible(true);
				moduleSource = field.get(b);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	protected void configure() {
//		 *     bind(Service.class).to(ServiceImpl.class).in(Singleton.class);
//		 *     bind(CreditCardPaymentService.class);
//		 *     bind(PaymentService.class).to(CreditCardPaymentService.class);
//		 *     bindConstant().annotatedWith(Names.named("port")).to(8080);
	}
	
	@Override
	public <T> void addSingleton(T serviceBean) {
		Class<T> clazz = getClass(serviceBean);
		b.bind(clazz).toInstance(serviceBean);
//		b.install(bn -> bn.bind(clazz).toInstance(serviceBean));
	}

	@SuppressWarnings("unchecked")
	private <T> Class<T> getClass(T serviceBean) {
		return (Class<T>) serviceBean.getClass();
	}

	@Override
	public <T> void addSingleton(String name, T serviceBean) {
		Class<T> clazz = getClass(serviceBean);
		
		b.bind(clazz).annotatedWith(Annotations.named(name)).toInstance(serviceBean);
//		b.bind(clazz).toInstance(serviceBean);
	}

	@Override
	public <T> void addService(Class<T> serviceBean, IServiceContext context) {
		b.bind(serviceBean);
	}

	@Override
	public <S, T extends S> void addService(String name, Class<T> serviceBean, IServiceContext context) {
		b.bind(serviceBean).annotatedWith(Annotations.named(name));
	}

	@Override
	public <S, F extends Provider<? extends S>> void addServiceFactory(Class<S> iface, String name,
			Class<F> factoryBean, IServiceContext context) {
		b.bind(iface).annotatedWith(Annotations.named(name)).toProvider(toProvider(iface, factoryBean));
	}

	@Override
	public <S, F extends Provider<? extends S>> void addServiceFactory(Class<S> iface,
			Class<F> factoryBean, IServiceContext context) {
		b.bind(iface).toProvider(toProvider(iface, factoryBean));
	}

	@Override
	public <S> S getService(Class<S> iface) {
		return injector.getInstance(iface);
	}

	@Override
	public <S> Optional<S> getOptionalService(Class<S> iface) {
		Binding<S> binding = injector.getBinding(iface);
//		binding.acceptVisitor(visitor)
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S> S getService(Class<S> iface, String name) {
		return injector.getInstance(Key.get(iface, Annotations.named(name)));
	}

	@Override
	public <S> Optional<S> getOptionalService(Class<S> iface, String name) {
		Binding<S> binding = injector.getBinding(Key.get(iface, Annotations.named(name)));
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S> Collection<S> getServices(Class<S> registrable) {
		List<Binding<S>> bindings = injector.findBindingsByType(TypeLiteral.get(registrable));
		return bindings.stream()
				.map(b -> b.getProvider().get())
				.collect(Collectors.toList());
	}
	
	private <S, F extends Provider<? extends S>> Provider<S> toProvider(Class<S> iface,	Class<F> factoryClass) {
		//TODO injection for bean
		return () -> LazyPojo.forProvider(factoryClass, toResolver()).getInstance();
	}

	@Override
	public IPropertyResolver toResolver() {
		return new GuicePropertyResolver(injector);
	}

	@Override
	public IBeanInjector toBeanInjector() {
		return new GuiceBeanInjector(injector);
	}
	
}

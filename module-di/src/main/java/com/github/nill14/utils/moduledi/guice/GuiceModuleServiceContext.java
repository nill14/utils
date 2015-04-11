package com.github.nill14.utils.moduledi.guice;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.IModule;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.internal.InternalInjectorCreator;
import com.google.inject.matcher.AbstractMatcher;

@SuppressWarnings("serial")
public class GuiceModuleServiceContext implements IServiceContext {
	
	private final IModule module;
	private final IServiceRegistry registry;
	
	private final AtomicBoolean ctxStarted = new AtomicBoolean(false);
	private GuicePropertyResolver propertyResolver;

	public GuiceModuleServiceContext(IModule module, IServiceRegistry registry) {
		this.module = module;
		this.registry = registry;

	}
	
	
	@Override
	public Optional<IPropertyResolver> getCustomResolver() {
		return Optional.of(contextResolver);
	}
	
	@Override
	public Optional<IPojoInitializer> getInitializer() {
		return Optional.of(contextInitializer);
	}
	

	
	private final IPropertyResolver contextResolver = new IPropertyResolver() {

		@Override
		public Object resolve(Object pojo, IParameterType<?> type) {
			if (propertyResolver != null) {
				return propertyResolver.resolve(pojo, type);
			}
			return null;
		}
		
	};

	private final IPojoInitializer contextInitializer = new IPojoInitializer() {
		
		@Override
		public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
			if (ctxStarted.compareAndSet(false, true)) {
//				Injector injector = Guice.createInjector((Module) module);
				Injector injector = new InternalInjectorCreator()
		        	.stage(Stage.TOOL)
		        	.addModules(Collections.singletonList(moduleProxy))
//		        	.parentInjector(parent)
		        	.build();
				propertyResolver = new GuicePropertyResolver(injector);
			}
		}
		
		@Override
		public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
			// nothing to do
		}
	};
	
	
	public static boolean isSupported(IModule module) {
		return module instanceof Module;
	}
	
	private final Module moduleProxy = new Module() {
		
		@Override
		public void configure(Binder binder) {
			((Module) module).configure(binder);
			binder.bindInterceptor(new AbstractMatcher<Class<?>>() {

				@Override
				public boolean matches(Class<?> t) {
					// TODO Auto-generated method stub
					return true;
				}
			}, new AbstractMatcher<Method>() {

				@Override
				public boolean matches(Method t) {
					// TODO Auto-generated method stub
					return true;
				}
			}, new MethodInterceptor() {
				
				@Override
				public Object invoke(MethodInvocation invocation) throws Throwable {
					// TODO Auto-generated method stub
					return null;
				}
			});
			
		}
	};
	
}
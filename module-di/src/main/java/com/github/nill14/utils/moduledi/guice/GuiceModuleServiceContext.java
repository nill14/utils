package com.github.nill14.utils.moduledi.guice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
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
	public Optional<AbstractPropertyResolver> getCustomResolver() {
		return Optional.of(contextResolver);
	}
	
	@Override
	public Optional<IPojoInitializer> getInitializer() {
		return Optional.of(contextInitializer);
	}
	

	
	private final AbstractPropertyResolver contextResolver = new AbstractPropertyResolver() {

		@Override
		public Object resolve(IParameterType type) {
			if (propertyResolver != null) {
				return propertyResolver.resolve(type);
			}
			return null;
		}

		@Override
		public IBeanInjector toBeanInjector() {
			if (propertyResolver != null) {
				return propertyResolver.toBeanInjector();
			}
			return null;
		}

		@Override
		public <T> void initializeBean(IBeanDescriptor<T> beanDescriptor, Object instance) {
			if (propertyResolver != null) {
				propertyResolver.initializeBean(beanDescriptor, instance);
			}
		}
		
		@Override
		public List<IPojoInitializer> getInitializers() {
			if (propertyResolver != null) {
				propertyResolver.getInitializers();
			}
			return Collections.emptyList();
		}
		
		@Override
		public <T> void destroyBean(IBeanDescriptor<T> beanDescriptor, Object instance) {
			if (propertyResolver != null) {
				propertyResolver.destroyBean(beanDescriptor, instance);
			}
		}

		@Override
		public void insertInitializer(IPojoInitializer initializer) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public void appendInitializer(IPojoInitializer extraInitializer) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		protected Object findByName(String name, IParameterType type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Object findByType(IParameterType type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Collection<?> findAllByType(IParameterType type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Object findByQualifier(IParameterType type, Annotation qualifier) {
			// TODO Auto-generated method stub
			return null;
		}		
	};

	private final IPojoInitializer contextInitializer = new IPojoInitializer() {
		
		@Override
		public <T> void init(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
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
		public <T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
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
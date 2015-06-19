package com.github.nill14.utils.moduledi.spring;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.ICallerContext;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
import com.github.nill14.utils.init.impl.ServiceRegistry;
import com.github.nill14.utils.moduledi.IModule;

@SuppressWarnings("serial")
public class SpringModuleServiceContext implements IServiceContext {
	
	private final IModule module;
	private final IServiceRegistry registry;
	
	private final AtomicBoolean ctxStarted = new AtomicBoolean(false);
	private ClassPathXmlApplicationContext ctx;
	private SpringPropertyResolver springPropertyResolver;

	public SpringModuleServiceContext(IModule module, IServiceRegistry registry) {
		this.module = module;
		this.registry = registry;
	}
	
	
	@Override
	public Optional<AbstractPropertyResolver> getCustomResolver() {
		return Optional.of(applicationContextResolver);
	}
	
	@Override
	public Optional<IPojoInitializer> getInitializer() {
		return Optional.of(applicationContextInitializer);
	}
	

	private void initApplicationContext(IServiceRegistry serviceRegistry) {
		String name = module.getClass().getSimpleName() + ".xml";
		InputStream inputStream = module.getClass().getClassLoader().getResourceAsStream(name);
		if (inputStream == null) {
			return;
		}
		GlobalBeanFactory parent = new GlobalBeanFactory((ServiceRegistry) serviceRegistry);
		
		ctx = new ClassPathXmlApplicationContext(new String[] {name}, false, parent);
		
		ctx.addBeanFactoryPostProcessor(new ModuleBeanDefinitionRegistryPostProcessor(serviceRegistry));
		springPropertyResolver = new SpringPropertyResolver(ctx);

			
//		@SuppressWarnings({ "unused", "resource" })
//		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext(resource);
		
//		   MyBean obj = new MyBean();
//		   ctx.autowireBean(obj);
		
	}
	
	
	private final AbstractPropertyResolver applicationContextResolver = new AbstractPropertyResolver() {

		@Override
		public Object resolve(IParameterType type, ICallerContext context) {
			if (springPropertyResolver == null) {
				//TODO this might be eventually the case when calling resolver from PojoFactory
				throw new IllegalStateException("Wrong order, first must be called initializer");
			}
			return springPropertyResolver.resolve(type, context);
		}
		
		@Override
		public <T> void initializeBean(IBeanDescriptor<T> beanDescriptor, Object instance, ICallerContext context) {
			if (springPropertyResolver == null) {
				//TODO this might be eventually the case when calling resolver from PojoFactory
				throw new IllegalStateException("Wrong order, first must be called initializer");
			}
			springPropertyResolver.initializeBean(beanDescriptor, instance, context);
		}
		
		@Override
		public IBeanInjector toBeanInjector() {
			if (springPropertyResolver == null) {
				//TODO this might be eventually the case when calling resolver from PojoFactory
				throw new IllegalStateException("Wrong order, first must be called initializer");
			}
			return springPropertyResolver.toBeanInjector();
		}
		
		@Override
		public List<IPojoInitializer> getInitializers() {
			if (springPropertyResolver == null) {
				//TODO this might be eventually the case when calling resolver from PojoFactory
				throw new IllegalStateException("Wrong order, first must be called initializer");
			}
			return springPropertyResolver.getInitializers();
		}

		@Override
		public <T> void destroyBean(IBeanDescriptor<T> beanDescriptor, Object instance) {
			if (springPropertyResolver == null) {
				//TODO this might be eventually the case when calling resolver from PojoFactory
				throw new IllegalStateException("Wrong order, first must be called initializer");
			}
			springPropertyResolver.destroyBean(beanDescriptor, instance);
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
		protected Object findByName(String name, IParameterType type, ICallerContext context) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Object findByType(IParameterType type, ICallerContext context) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Collection<?> findAllByType(IParameterType type, ICallerContext context) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Object findByQualifier(IParameterType type, Annotation qualifier, ICallerContext context) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};

	private final IPojoInitializer applicationContextInitializer = new IPojoInitializer() {
		
		@Override
		public <T> void init(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance, ICallerContext context) {
			if (ctxStarted.compareAndSet(false, true)) {
				initApplicationContext(registry);
				if (ctx != null) {
					ctx.refresh();
				}
			}
		}
		
		@Override
		public <T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
			// nothing to do
		}
	};
	
	
	public static boolean isSupported(IModule module) {
		String name = module.getClass().getSimpleName() + ".xml";
		InputStream inputStream = module.getClass().getClassLoader().getResourceAsStream(name);
		return inputStream != null;
	}
	
}
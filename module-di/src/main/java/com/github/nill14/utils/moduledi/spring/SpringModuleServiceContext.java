package com.github.nill14.utils.moduledi.spring;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
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
	public Optional<IPropertyResolver> getCustomResolver() {
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
	
	
	private final IPropertyResolver applicationContextResolver = new IPropertyResolver() {

		@Override
		public Object resolve(IParameterType type) {
			if (springPropertyResolver == null) {
				//TODO this might be eventually the case when calling resolver from PojoFactory
				throw new IllegalStateException("Wrong order, first must be called initializer");
			}
			return springPropertyResolver.resolve(type);
		}
		
		@Override
		public void initializeBean(Object instance) {
			if (springPropertyResolver == null) {
				//TODO this might be eventually the case when calling resolver from PojoFactory
				throw new IllegalStateException("Wrong order, first must be called initializer");
			}
			springPropertyResolver.initializeBean(instance);
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
		
	};

	private final IPojoInitializer applicationContextInitializer = new IPojoInitializer() {
		
		@Override
		public void init(IPojoFactory<?> pojoFactory, Object instance) {
			if (ctxStarted.compareAndSet(false, true)) {
				initApplicationContext(registry);
				if (ctx != null) {
					ctx.refresh();
				}
			}
		}
		
		@Override
		public void destroy(IPojoFactory<?> pojoFactory, Object instance) {
			// nothing to do
		}
	};
	
	
	public static boolean isSupported(IModule module) {
		String name = module.getClass().getSimpleName() + ".xml";
		InputStream inputStream = module.getClass().getClassLoader().getResourceAsStream(name);
		return inputStream != null;
	}
	
}
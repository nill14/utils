package com.github.nill14.utils.moduledi;

import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.api.IType;
import com.github.nill14.utils.init.impl.ServiceRegistry;
import com.github.nill14.utils.moduledi.spring.GlobalBeanFactory;
import com.github.nill14.utils.moduledi.spring.ModuleBeanDefinitionRegistryPostProcessor;
import com.github.nill14.utils.moduledi.spring.SpringPropertyResolver;

@SuppressWarnings("serial")
public class ModuleServiceContext implements IServiceContext {
	
	private final AbstractModule module;
	private final IServiceRegistry registry;
	
	private final AtomicBoolean ctxStarted = new AtomicBoolean(false);
	private ClassPathXmlApplicationContext ctx;
	private SpringPropertyResolver springPropertyResolver;

	public ModuleServiceContext(AbstractModule module, IServiceRegistry registry) {
		this.module = module;
		this.registry = registry;
	}
	
	
	@Override
	public Optional<IPropertyResolver> getCustomResolver() {
		return Optional.of(applicationContextResolver);
	}
	
	@Override
	public Optional<IPojoInitializer<Object>> getInitializer() {
		return Optional.of(applicationContextInitializer);
	}
	

	private void initApplicationContext(IServiceRegistry serviceRegistry) {
		String name = module.getClass().getSimpleName() + ".xml";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(name);
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
		public Object resolve(Object pojo, IType type) {
			if (springPropertyResolver != null) {
				return springPropertyResolver.resolve(pojo, type);
			}
			return null;
		}
		
	};

	private final IPojoInitializer<Object> applicationContextInitializer = new IPojoInitializer<Object>() {
		
		@Override
		public void init(ILazyPojo<?> lazyPojo, Object instance) {
			if (ctxStarted.compareAndSet(false, true)) {
				initApplicationContext(registry);
				if (ctx != null) {
					ctx.refresh();
				}
			}
		}
		
		@Override
		public void destroy(ILazyPojo<?> lazyPojo, Object instance) {
			// nothing to do
		}
	};
	
	
	
	
}
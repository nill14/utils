package com.github.nill14.utils.moduledi;

import java.io.InputStream;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.nill14.parsers.dependency.IDependencyDescriptor;
import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.parsers.dependency.impl.DependencyDescriptor;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.ServiceRegistry;
import com.github.nill14.utils.moduledi.spring.GlobalBeanFactory;
import com.github.nill14.utils.moduledi.spring.ModuleBeanDefinitionRegistryPostProcessor;

public abstract class AbstractModule extends com.google.inject.AbstractModule {

	private IDependencyDescriptor<Class<?>> dependencyDescriptor;
	
	
	public IDependencyDescriptor<Class<?>> getDependencyDescriptor() {
		return dependencyDescriptor;
	}
	
	public void prepareModule(IServiceRegistry registry) {
		IDependencyDescriptorBuilder<Class<?>> dependencyBuilder = DependencyDescriptor.builder(getClass());
		IServiceBuilder serviceBuilder = new ServiceBuilder();
		buildServices(serviceBuilder);
		serviceBuilder.buildDependencies(dependencyBuilder);
		buildDependencies(dependencyBuilder);
		dependencyDescriptor = dependencyBuilder.build();
		serviceBuilder.buildServices(registry);
	}
	
	public void startModule(IServiceRegistry registry) {
		
		initApplicationContext(registry);
	}
	
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {}
	
	public abstract void buildServices(IServiceBuilder builder);
	
	
	protected void initApplicationContext(IServiceRegistry serviceRegistry) {
		String name = getClass().getSimpleName() + ".xml";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(name);
		if (inputStream == null) {
			return;
		}
		GlobalBeanFactory parent = new GlobalBeanFactory((ServiceRegistry) serviceRegistry);
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {name}, false, parent);
		
		ctx.addBeanFactoryPostProcessor(new ModuleBeanDefinitionRegistryPostProcessor(serviceRegistry));
//		ctx.getAutowireCapableBeanFactory().autowireBeanProperties(existingBean, autowireMode, dependencyCheck);reBean(object)
		
		refreshContext(ctx, serviceRegistry);

			
//		@SuppressWarnings({ "unused", "resource" })
//		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext(resource);
		
//		   MyBean obj = new MyBean();
//		   ctx.autowireBean(obj);
	}
	
	
	protected void refreshContext(ClassPathXmlApplicationContext ctx, IServiceRegistry serviceRegistry) {
		ctx.refresh();
	}
}

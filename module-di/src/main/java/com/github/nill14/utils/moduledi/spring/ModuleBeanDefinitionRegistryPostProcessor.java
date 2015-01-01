package com.github.nill14.utils.moduledi.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.ServiceRegistry;

public class ModuleBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	private final ServiceRegistry serviceRegistry;
	private final IPropertyResolver resolver;

	public ModuleBeanDefinitionRegistryPostProcessor(IServiceRegistry serviceRegistry) {
		this.serviceRegistry = (ServiceRegistry) serviceRegistry;
		this.resolver = this.serviceRegistry.toResolver();
	}
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.registerScope("conversation", new ConversationScope());
		beanFactory.registerSingleton("registry", serviceRegistry);
		beanFactory.registerSingleton("moduleAnnotationBeanPostProcessor", new ModuleAnnotationBeanPostProcessor(resolver));
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		
//		for (Class<?> bean : serviceRegistry.getBeans()) { //FIXME returns proxy
//			ModuleBeanDefinition beanDefinition = new ModuleBeanDefinition(bean);
//			beanDefinition.setScope("conversation"); //FIXME
//			
//			registry.registerBeanDefinition(bean.getSimpleName(), beanDefinition);
//		}

	}

}

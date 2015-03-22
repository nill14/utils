package com.github.nill14.utils.moduledi.scope;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.ServiceRegistry;

public class ExtraScopesBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {

	private final IServiceRegistry serviceRegistry;
	private final IPropertyResolver resolver;

	public ExtraScopesBeanPostProcessor(IServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		resolver = this.serviceRegistry.toResolver();
	}
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.registerScope("SLA", new SLAScope());
		beanFactory.registerScope("Ticket", new TicketScope());
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

	}
	

}

package com.github.nill14.utils.moduledi.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;

import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.ServiceRegistry;

public class ModuleBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	private final ServiceRegistry serviceRegistry;
	private final IPropertyResolver resolver;

	public ModuleBeanDefinitionRegistryPostProcessor(IServiceRegistry serviceRegistry) {
		this.serviceRegistry = (ServiceRegistry) serviceRegistry;
		resolver = this.serviceRegistry.toResolver();
	}
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.registerScope("conversation", new ConversationScope());
		beanFactory.registerSingleton("registry", serviceRegistry);
		beanFactory.registerSingleton("moduleAnnotationBeanPostProcessor", new ModuleAnnotationBeanPostProcessor(resolver));
		

		
//		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
//		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
//
//		// BeanFactory interface not registered as resolvable type in a plain factory.
//		// MessageSource registered (and found for autowiring) as a bean.
//		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
//		beanFactory.registerResolvableDependency(ResourceLoader.class, this);
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		
		if (!registry.containsBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
			/* annotation config might be activated as well by 
			 * <context:annotation-config> or by 
			 * <context:component-scan>.*/
			AnnotationConfigUtils.registerAnnotationConfigProcessors(registry, this);
		}
//		if (!registry.containsBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
//			RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
//			def.setSource(source);
//			beanDefs.add(registerPostProcessor(registry, def, AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
//		}
//		
//		for (Class<?> bean : serviceRegistry.getBeans()) { //FIXME returns proxy
//			ModuleBeanDefinition beanDefinition = new ModuleBeanDefinition(bean);
//			beanDefinition.setScope("conversation"); //FIXME
//			
//			registry.registerBeanDefinition(bean.getSimpleName(), beanDefinition);
//		}

	}
	

	private static BeanDefinitionHolder registerPostProcessor(
			BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {

		definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		registry.registerBeanDefinition(beanName, definition);
		return new BeanDefinitionHolder(definition, beanName);
	}

}

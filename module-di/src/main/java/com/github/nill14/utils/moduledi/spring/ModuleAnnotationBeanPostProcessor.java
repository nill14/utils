package com.github.nill14.utils.moduledi.spring;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.inject.FieldInjectionDescriptor;
import com.github.nill14.utils.init.inject.PojoInjectionDescriptor;

public class ModuleAnnotationBeanPostProcessor implements BeanPostProcessor, 
	MergedBeanDefinitionPostProcessor, BeanFactoryAware, InstantiationAwareBeanPostProcessor {

	
	private final IPropertyResolver resolver;
	private ConfigurableListableBeanFactory beanFactory;
	
	public ModuleAnnotationBeanPostProcessor(IPropertyResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
			throw new IllegalArgumentException(
					"AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
		}
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		PojoInjectionDescriptor pojoDescriptor = new PojoInjectionDescriptor(bean.getClass());
		
		for (FieldInjectionDescriptor injector : pojoDescriptor.getFieldDescriptors() ) {
			
			Object value = beanFactory.getBean(injector.getName(), injector.getRawType());
			
			if (value == null) {
				value = beanFactory.getBean(injector.getRawType());
			}
			
			if (value == null) {
				value = resolver.resolve(bean, injector);
			}
			
			if (value != null) {
				injector.inject(bean, value);
			
			} else {
				throw new RuntimeException(); //TODO
			}
		}
		
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType,
			String beanName) {
//		System.out.println(beanName);
	}

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds,
			Object bean, String beanName) throws BeansException {
		return pvs;
	}

}

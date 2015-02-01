package com.github.nill14.utils.moduledi.spring;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import com.github.nill14.utils.init.impl.ServiceRegistry;

public class GlobalBeanFactory implements ApplicationContext {

	private final ServiceRegistry registry;

	public GlobalBeanFactory(ServiceRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	public Object getBean(String name) throws BeansException {
		return registry.getOptionalService(Object.class, name)
				.orElseThrow(() -> new BeanCreationException(name));
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		Object bean = registry.getBean(name);
		
		if (bean == null) {
			throw new BeanCreationException(name);
		}
		
		if (requiredType != null && !requiredType.isAssignableFrom(bean.getClass())) {
			throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
		}
		return (T) bean;
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return registry.getOptionalService(requiredType)
				.orElseThrow(() -> new BeanCreationException(requiredType.toGenericString()));
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		throw new BeanCreationException("unsupported");
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		throw new BeanCreationException("unsupported");
	}

	@Override
	public boolean containsBean(String name) {
		return registry.getBean(name) != null;
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return false;
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return false;
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
		Object bean = registry.getBean(name);
		return bean != null && targetType.isAssignableFrom(bean.getClass());
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		Object bean = registry.getBean(name);
		if (bean != null) {
			return bean.getClass();
		}
		else {
			return null;
		}
	}

	@Override
	public String[] getAliases(String name) {
		return new String[0];
	}

	@Override
	public Environment getEnvironment() {
		return null;
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return registry.getBeanNames().contains(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return registry.getBeanNames().stream().toArray(String[]::new);
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {
		throw new UnsupportedOperationException(type.toGenericString());
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		return registry.getBeansOfType(type).keySet().stream().toArray(String[]::new);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		return registry.getBeansOfType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons,
			boolean allowEagerInit) throws BeansException {
		return registry.getBeansOfType(type);
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		throw new UnsupportedOperationException(annotationType.toGenericString());
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
			throws BeansException {
		throw new UnsupportedOperationException(annotationType.toGenericString());
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException {
		throw new UnsupportedOperationException(beanName);
	}

	@Override
	public BeanFactory getParentBeanFactory() {
		return null;
	}

	@Override
	public boolean containsLocalBean(String name) {
		return containsBean(name);
	}

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void publishEvent(ApplicationEvent event) {
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource getResource(String location) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ClassLoader getClassLoader() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getApplicationName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDisplayName() {
		return getClass().toGenericString();
	}

	@Override
	public long getStartupDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ApplicationContext getParent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		throw new IllegalStateException();
	}

}

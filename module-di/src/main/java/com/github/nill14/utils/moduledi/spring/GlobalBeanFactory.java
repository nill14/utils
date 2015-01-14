package com.github.nill14.utils.moduledi.spring;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import com.github.nill14.utils.init.api.IServiceRegistry;

public class GlobalBeanFactory implements ApplicationContext {

	private final IServiceRegistry registry;

	public GlobalBeanFactory(IServiceRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	public Object getBean(String name) throws BeansException {
		return registry.getOptionalService(Object.class, name)
				.orElseThrow(() -> new BeanCreationException(name));
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		if (requiredType == null) {
			requiredType = (Class<T>) Object.class;
		}
		return registry.getOptionalService(requiredType, name)
				.orElseThrow(() -> new BeanCreationException(name));
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
		return registry.getOptionalService(Object.class, name).isPresent();
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return containsBean(name);
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return false;
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAliases(String name) {
		return new String[0];
	}

	@Override
	public Environment getEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		throw new UnsupportedOperationException(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return 0;
	}

	@Override
	public String[] getBeanDefinitionNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {
		throw new UnsupportedOperationException(type.toGenericString());
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		// TODO Auto-generated method stub
		return new String[] {"snackService"};//FIXME
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons,
			boolean allowEagerInit) throws BeansException {
		throw new UnsupportedOperationException(type.toGenericString());
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
		throw new UnsupportedOperationException(name);
	}

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void publishEvent(ApplicationEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getResource(String location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getApplicationName() {
		return "";
	}

	@Override
	public String getDisplayName() {
		return "global";
	}

	@Override
	public long getStartupDate() {
		return 0;
	}

	@Override
	public ApplicationContext getParent() {
		return null;
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		throw new IllegalStateException();
	}

}

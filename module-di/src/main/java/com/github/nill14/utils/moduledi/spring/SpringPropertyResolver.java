package com.github.nill14.utils.moduledi.spring;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Provider;

import org.springframework.context.ApplicationContext;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class SpringPropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {
	
	private final ApplicationContext context;

	public SpringPropertyResolver(ApplicationContext context) {
		this.context = context;
	}

	@Override
	protected Provider<?> doResolveQualifiers(Object pojo, IParameterType<?> type, Class<?> clazz) {
		Provider<?> result = IPropertyResolver.nullProvider();
		
		for (Annotation qualifier : type.getQualifiers()) {
			Provider<?> query = IPropertyResolver.nullProvider();
			if (Named.class.equals(qualifier.annotationType())) {
				String name = ((Named) qualifier).value();
				query = findByName(pojo, name, clazz);
			} else {
				query = findByQualifier(pojo, clazz, qualifier);
			}
			
			if (result != IPropertyResolver.nullProvider() && !result.equals(query)) {
				return IPropertyResolver.nullProvider();
			} else {
				result = query;
			}
		}
		
		return result;
	}

	@Override
	protected Provider<?> findByQualifier(Object pojo, Class<?> type, Annotation qualifier) {
		
		Class<? extends Annotation> annotationClass = qualifier.annotationType();
		Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(annotationClass);
		
		Map<String, ?> beansOfType = context.getBeansOfType(type);
		Collection<Object> values = Maps.difference(beansWithAnnotation, beansOfType).entriesInCommon().values();
		
		List<Object> result = values.stream()
				.filter(obj -> qualifier.equals(obj.getClass().getAnnotation(annotationClass)))
				.collect(Collectors.toList());
		
		if (result.isEmpty()) {
			return IPropertyResolver.nullProvider();
		
		} else if (result.size() == 1) {
			return provider(result.get(0));
			
		} else {
			throw new IllegalStateException("Expected one result, got "+ result);
		}
	}
	
	

	@Override
	protected Provider<?> findByName(Object pojo, String name, Class<?> type) {
		if (context.isTypeMatch(name, type)) {
			return provider(context.getBean(name, type));
		}
		return IPropertyResolver.nullProvider();
	}

	@Override
	protected Provider<?> findByType(Object pojo, IParameterType<?> type, Class<?> clazz) {
		String[] names = context.getBeanNamesForType(clazz);
		if (names.length > 0) {
			return provider(context.getBean(names[0], clazz));
		}
		return IPropertyResolver.nullProvider();
	}

	@Override
	protected Collection<?> findAllByType(Object pojo, Class<?> type) {
		return context.getBeansOfType(type).values();
	}



}

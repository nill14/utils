package com.github.nill14.utils.moduledi.spring;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Named;

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
	protected Object doResolveQualifiers(IParameterType<?> type, Class<?> clazz) {
		Object result = null;
		
		for (Annotation qualifier : type.getQualifiers()) {
			Object query = null;
			if (Named.class.equals(qualifier.annotationType())) {
				String name = ((Named) qualifier).value();
				query = findByName(name, clazz);
			} else {
				query = findByQualifier(clazz, qualifier);
			}
			
			if (result != null && !result.equals(query)) {
				return null;
			} else {
				result = query;
			}
		}
		
		return result;
	}

	@Override
	protected Object findByQualifier(Class<?> type, Annotation qualifier) {
		
		Class<? extends Annotation> annotationClass = qualifier.annotationType();
		Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(annotationClass);
		
		Map<String, ?> beansOfType = context.getBeansOfType(type);
		Collection<Object> values = Maps.difference(beansWithAnnotation, beansOfType).entriesInCommon().values();
		
		List<Object> result = values.stream()
				.filter(obj -> qualifier.equals(obj.getClass().getAnnotation(annotationClass)))
				.collect(Collectors.toList());
		
		if (result.isEmpty()) {
			return null;
		
		} else if (result.size() == 1) {
			return result.get(0);
			
		} else {
			throw new IllegalStateException("Expected one result, got "+ result);
		}
	}
	
	

	@Override
	protected Object findByName(String name, Class<?> type) {
		if (context.isTypeMatch(name, type)) {
			return context.getBean(name, type);
		}
		return null;
	}

	@Override
	protected Object findByType(IParameterType<?> type) {
		Class<?> clazz = type.getRawType();
		String[] names = context.getBeanNamesForType(clazz);
		if (names.length > 0) {
			return context.getBean(names[0], clazz);
		}
		return null;
	}

	@Override
	protected Collection<?> findAllByType(Class<?> type) {
		return context.getBeansOfType(type).values();
	}



}

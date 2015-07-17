package com.github.nill14.utils.moduledi.spring;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;
import com.github.nill14.utils.init.impl.CallerContext;
import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class SpringPropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {
	
	private final ApplicationContext context;

	public SpringPropertyResolver(ApplicationContext context) {
		this.context = context;
	}

	@Override
	protected Object findByQualifier(IParameterType type, Annotation qualifier, CallerContext context) {
		
		Class<? extends Annotation> annotationClass = qualifier.annotationType();
		Map<String, Object> beansWithAnnotation = this.context.getBeansWithAnnotation(annotationClass);
		
		Map<String, ?> beansOfType = this.context.getBeansOfType(type.getRawType());
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
	protected Object findByName(String name, IParameterType type, CallerContext context) {
		if (this.context.isTypeMatch(name, type.getRawType())) {
			return this.context.getBean(name, type);
		}
		return null;
	}

	@Override
	protected Object findByType(IParameterType type, CallerContext context) {
		Class<?> clazz = type.getRawType();
		String[] names = this.context.getBeanNamesForType(clazz);
		if (names.length > 0) {
			return this.context.getBean(names[0], clazz);
		}
		return null;
	}

	@Override
	protected Collection<?> findAllByType(IParameterType type, CallerContext context) {
		return this.context.getBeansOfType(type.getRawType()).values();
	}



}

package com.github.nill14.utils.moduledi.spring;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.impl.AbstractPropertyResolver;

@SuppressWarnings("serial")
public class SpringPropertyResolver extends AbstractPropertyResolver implements IPropertyResolver {
	
	private final ApplicationContext context;

	public SpringPropertyResolver(ApplicationContext context) {
		this.context = context;
	}


	@Override
	protected Object findByName(String name, Class<?> type) {
		if (context.isTypeMatch(name, type)) {
			return context.getBean(name, type);
		}
		return null;
	}

	@Override
	protected Object findByType(Class<?> type) {
		String[] names = context.getBeanNamesForType(type);
		if (names.length > 0) {
			return context.getBean(names[0], type);
		}
		return null;
	}

	@Override
	protected Collection<?> findAllByType(Class<?> type) {
		return context.getBeansOfType(type).values();
	}

}

package com.github.nill14.utils.init.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.meta.EventBusSubscriber;
import com.google.common.eventbus.EventBus;

@SuppressWarnings("serial")
public final class EventBusPojoInitializer implements IPojoInitializer {

	private static final Logger log = LoggerFactory.getLogger(EventBusPojoInitializer.class);
	
	@Override
	public <T> void init(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance, CallerContext context) {
		EventBusSubscriber annotation = instance.getClass().getAnnotation(EventBusSubscriber.class);
		if (annotation != null) {
			EventBus eventBus = (EventBus) resolver.resolve(IParameterType.of(EventBus.class), context);
			if (eventBus != null) {
				eventBus.register(instance);
			} else {
				log.warn("EventBus is not resolved.");
			}
		}
	}

	@Override
	public <T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
		EventBusSubscriber annotation = instance.getClass().getAnnotation(EventBusSubscriber.class);
		if (annotation != null) {
			EventBus eventBus = (EventBus) resolver.resolve(IParameterType.of(EventBus.class), CallerContext.prototype());
			if (eventBus != null) {
				eventBus.unregister(instance);
			} else {
				log.warn("EventBus is not resolved.");
			}
		}
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof EventBusPojoInitializer;
	}

}

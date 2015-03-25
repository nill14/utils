package com.github.nill14.utils.init.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.meta.EventBusSubscriber;
import com.google.common.eventbus.EventBus;

@SuppressWarnings("serial")
public class EventBusPojoInitializer implements IPojoInitializer<Object> {

	private static final Logger log = LoggerFactory.getLogger(EventBusPojoInitializer.class);
	
	@Override
	public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
		EventBusSubscriber annotation = instance.getClass().getAnnotation(EventBusSubscriber.class);
		if (annotation != null) {
			IPropertyResolver resolver = pojoFactory.getResolver();
			EventBus eventBus = (EventBus) resolver.resolve(this, IParameterType.of(EventBus.class));
			if (eventBus != null) {
				eventBus.register(instance);
			} else {
				log.warn("EventBus is not resolved.");
			}
		}
	}

	@Override
	public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
		EventBusSubscriber annotation = instance.getClass().getAnnotation(EventBusSubscriber.class);
		if (annotation != null) {
			IPropertyResolver resolver = pojoFactory.getResolver();
			EventBus eventBus = (EventBus) resolver.resolve(this, IParameterType.of(EventBus.class));
			if (eventBus != null) {
				eventBus.unregister(instance);
			} else {
				log.warn("EventBus is not resolved.");
			}
		}
	}

}

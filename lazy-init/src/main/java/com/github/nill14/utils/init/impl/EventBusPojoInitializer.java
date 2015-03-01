package com.github.nill14.utils.init.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IType;
import com.github.nill14.utils.init.meta.EventBusSubscriber;
import com.google.common.eventbus.EventBus;

public class EventBusPojoInitializer implements IPojoInitializer<Object> {

	private static final long serialVersionUID = -6999206469201978450L;
	private static final Logger log = LoggerFactory.getLogger(EventBusPojoInitializer.class);
	
	public static EventBusPojoInitializer withResolver(IPropertyResolver resolver) {
		return new EventBusPojoInitializer(resolver);
	}
	
	private final IPropertyResolver resolver;

	private EventBusPojoInitializer(IPropertyResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public void init(ILazyPojo<?> lazyPojo, Object instance) {
		EventBusSubscriber annotation = instance.getClass().getAnnotation(EventBusSubscriber.class);
		if (annotation != null) {
			EventBus eventBus = (EventBus) this.resolver.resolve(this, IType.fromClass(EventBus.class));
			if (eventBus != null) {
				eventBus.register(instance);
			} else {
				log.warn("EventBus is not resolved.");
			}
		}
	}

	@Override
	public void destroy(ILazyPojo<?> lazyPojo, Object instance) {
		EventBusSubscriber annotation = instance.getClass().getAnnotation(EventBusSubscriber.class);
		if (annotation != null) {
			EventBus eventBus = (EventBus) this.resolver.resolve(this, IType.fromClass(EventBus.class));
			if (eventBus != null) {
				eventBus.unregister(instance);
			} else {
				log.warn("EventBus is not resolved.");
			}
		}
	}

}

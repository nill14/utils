package com.github.nill14.utils.init.impl;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.PrintStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.meta.EventBusSubscriber;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventBusTest {

	private static final Logger log = LoggerFactory.getLogger(EventBusTest.class);
	private IBeanInjector beanInjector;
	private EventBus eventBus;
	private PrintStream printStream;
	private IServiceRegistry serviceRegistry;
	
	@BeforeMethod
	public void prepare() {
		serviceRegistry = IServiceRegistry.newRegistry();
		serviceRegistry.addSingleton(eventBus = new EventBus());
		serviceRegistry.addSingleton(printStream = spy(System.out));
		beanInjector = serviceRegistry.toBeanInjector(CallerContext.prototype());
		
	}
	
	@Test
	public void testClassic() {
		beanInjector.getInstance(EventBusSubscriberClassic.class);
		
		eventBus.post(new SampleEvent());
		verify(printStream).printf(anyString(), Mockito.anyVararg());
	}
	
	@Test
	public void testExtended() {
		assertNotNull(serviceRegistry.getOptionalService(EventBus.class));
		assertNotNull(serviceRegistry.toResolver().resolve(IParameterType.of(EventBus.class), CallerContext.prototype()));
		
		beanInjector.getInstance(EventBusSubscriberExtended.class);
		
		eventBus.post(new SampleEvent());
		verify(printStream, atLeastOnce()).printf(anyString(), Mockito.anyVararg());
	}
	
	public static class SampleEvent {
		
	}
	
	public static class EventBusSubscriberClassic {
		@Inject
		EventBus eventBus;
		
		@Inject
		PrintStream out;
		
		@PostConstruct
		public void init() {
			eventBus.register(this);
		}
		
		@Subscribe
		public void handle(SampleEvent event) {
			out.printf("%s: %s\n", this, event);
		}
		
		
		@PreDestroy
		public void destroy() {
			eventBus.unregister(this);
		}
	}
	
	@EventBusSubscriber
	public static class EventBusSubscriberExtended {
		
		@Inject
		PrintStream out;
		
		@Subscribe
		public void handle(SampleEvent event) {
			out.printf("%s: %s\n", this, event);
		}
		
	}
}

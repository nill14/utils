package com.github.nill14.utils.init.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nill14.utils.init.ITimeService;
import com.github.nill14.utils.init.TimeService;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceRegistry;

public class AnnotationPojoInitializerTest {

	private static final Logger log = LoggerFactory.getLogger(AnnotationPojoInitializerTest.class);
	private ITimeService timeService;
	private ILazyPojo<TimeService> lazyPojo;
	private TimeService spy;
	private IPropertyResolver resolver;
	
	@Before
	public void init() {
		new TimeService();
		spy = mock(TimeService.class);

		IServiceRegistry registry = IServiceRegistry.newRegistry();
		registry.addSingleton(ZoneId.systemDefault());
		registry.addSingleton("greeting", "greeting");
		registry.addSingleton(spy);
		resolver = registry.toResolver();
		
		IPojoInitializer initializer = IPojoInitializer.standard();
		lazyPojo = LazyPojo.forBean(TimeService.class, resolver, initializer);
		timeService = LazyJdkProxy.newProxy(ITimeService.class, lazyPojo);
	}
	
	@After
	public void destroy() {
		lazyPojo.freeInstance();
	}
	
	@Test
	public void testIntegration() {
		IPojoInitializer initializer = IPojoInitializer.standard();
		ILazyPojo<TimeService> lazyPojo = LazyPojo.forBean(TimeService.class, resolver, initializer);
		ITimeService timeService = LazyJdkProxy.newProxy(ITimeService.class, lazyPojo);
		LocalDateTime now = timeService.getNow();
		log.info("testIntegration: {}", now);
		assertNotNull(now);
	}
	
	@Test
	public void testNow() {
		LocalDateTime now = timeService.getNow();
		log.info("testNow: {}", now);
		assertNotNull(now);
	}
	
	@Test
	public void testPostConstruct() {
		log.info("testPostConstruct");
		timeService.getNow();
		verify(spy, times(1)).init();
		verify(spy, never()).destroy();
	}
	
	@Test
	public void testPreDestroy() {
		log.info("testPreDestroy");
		lazyPojo.getInstance();
		lazyPojo.freeInstance();
		verify(spy, times(1)).init();
		verify(spy, times(1)).destroy();
	}
	
	@Test
	public void testInject() {
		log.info("testInject");
		TimeService service = lazyPojo.getInstance();
		assertNotNull(service.getZone());
		assertNotNull(service.getGreeting());
	}
}

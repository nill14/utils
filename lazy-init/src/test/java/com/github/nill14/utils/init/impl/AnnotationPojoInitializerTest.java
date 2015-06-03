package com.github.nill14.utils.init.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.ITimeService;
import com.github.nill14.utils.init.InitSpy;
import com.github.nill14.utils.init.TimeService;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.binding.TestBinder;
import com.github.nill14.utils.init.meta.Annotations;

public class AnnotationPojoInitializerTest {

	private static final Logger log = LoggerFactory.getLogger(AnnotationPojoInitializerTest.class);
	private ITimeService timeService;
	private ILazyPojo<TimeService> lazyPojo;
	private InitSpy spy;
	private IPropertyResolver resolver;
	
	@BeforeMethod
	public void init() {
		new TimeService();
		spy = mock(InitSpy.class);

		TestBinder b = new TestBinder();
		
		b.bind(ZoneId.class).toInstance(ZoneId.systemDefault());
		b.bind(String.class).annotatedWith(Annotations.named("greeting")).toInstance("greeting");
		b.bind(InitSpy.class).toInstance(spy);
		
		resolver = b.toResolver();
		
		lazyPojo = LazyPojo.forBean(TimeService.class, resolver);
		timeService = LazyJdkProxy.newProxy(ITimeService.class, lazyPojo);
	}
	
	@AfterMethod
	public void destroy() {
		lazyPojo.freeInstance();
	}
	
	@Test
	public void testIntegration() {
		ILazyPojo<TimeService> lazyPojo = LazyPojo.forBean(TimeService.class, resolver);
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

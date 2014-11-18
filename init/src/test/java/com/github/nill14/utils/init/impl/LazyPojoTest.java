package com.github.nill14.utils.init.impl;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nill14.utils.init.GreeterFactory;
import com.github.nill14.utils.init.ICalc;
import com.github.nill14.utils.init.IGreeter;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;

public class LazyPojoTest {
	
	private static final String DESTROYED = "destroyed";
	private static final String GREETING = "Hello World!";
	private static final Logger log = LoggerFactory.getLogger(LazyPojoTest.class);
	private static ExecutorService executor = Executors.newCachedThreadPool();
	private static AtomicInteger instances = new AtomicInteger();
	private static ILazyPojo<IGreeter> lazyPojo;

	private static IPojoInitializer<GreeterFactory> factoryInitializer = new IPojoInitializer<GreeterFactory>() {
		
		@Override
		public void init(GreeterFactory instance) {
			assertThat(instance.getGreeting(), not(GREETING));
			instances.incrementAndGet();
			instance.setGreeting(GREETING);
		}
		
		@Override
		public void destroy(GreeterFactory instance) {
			assertThat(instance.getGreeting(), is(GREETING));
			instances.decrementAndGet();
			instance.setGreeting(DESTROYED);
		}
	};
	
	@BeforeClass
	public static void setUp() {
		lazyPojo = LazyPojo.forFactory(IGreeter.class, GreeterFactory.class, factoryInitializer);
	}
	
	@Before
	public void init() {
		lazyPojo.freeInstance();
		assertEquals(0, instances.get());
	}

	@Test
	public void testGreet() {
		assertEquals(GREETING, lazyPojo.getInstance().sayGreeting());
		assertEquals(1, instances.get());
	}
	

	@Test
	public void testInit() throws InterruptedException, ExecutionException {
		Future<IGreeter> future = lazyPojo.init(executor);
		future.get();
		assertEquals(1, instances.get());
		assertEquals(GREETING, lazyPojo.getInstance().sayGreeting());
		assertEquals(1, instances.get());
	}
	
	@Test
	public void testDestroy() throws InterruptedException, ExecutionException {
		lazyPojo.init(executor).get().toString();
		boolean destroyed = lazyPojo.destroy(executor).get();
		assertTrue(destroyed);
		assertEquals(0, instances.get());
		assertEquals(GREETING, lazyPojo.getInstance().sayGreeting());
		assertEquals(1, instances.get());
	}
	
	@Test
	public void stressTest() throws InterruptedException, ExecutionException {
		lazyPojo.init(executor);
		lazyPojo.init(executor);
		lazyPojo.init(executor);
		lazyPojo.destroy(executor);
		lazyPojo.destroy(executor);
		lazyPojo.init(executor);
		lazyPojo.destroy(executor);
		lazyPojo.init(executor);
		lazyPojo.destroy(executor);
		lazyPojo.destroy(executor);
		lazyPojo.destroy(executor);
		lazyPojo.init(executor);
		lazyPojo.init(executor);
		lazyPojo.init(executor);
		lazyPojo.destroy(executor);
		lazyPojo.init(executor);
		lazyPojo.destroy(executor);
		
		int actual = instances.get();
		log.info("actual (stress): {}", actual);
		assertThat(actual, anyOf(is(0), is(1)));
		assertEquals(GREETING, lazyPojo.getInstance().sayGreeting());
		assertEquals(1, instances.get());
	}
}

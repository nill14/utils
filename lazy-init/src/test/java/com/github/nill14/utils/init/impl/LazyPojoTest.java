package com.github.nill14.utils.init.impl;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.GreeterFactory;
import com.github.nill14.utils.init.IGreeter;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;

@SuppressWarnings("serial")
@Test(invocationCount = 10)
public class LazyPojoTest {
	
	private static final String DESTROYED = "destroyed";
	private static final String GREETING = "Hello World!";
	private static final Logger log = LoggerFactory.getLogger(LazyPojoTest.class);
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private ILazyPojo<IGreeter> lazyPojo;
	private AtomicInteger instances;

	private IPojoInitializer factoryInitializer; 
			
	class CountingPojoInitializer implements IPojoInitializer {
		
		@Override
		public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
			//instance could be also IGreeter
			if (instance instanceof GreeterFactory) {
				GreeterFactory factory = (GreeterFactory) instance;
				assertThat(factory.getGreeting(), not(GREETING));
				instances.incrementAndGet();
				factory.setGreeting(GREETING);
			}
		}
		
		@Override
		public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
			//instance could be also IGreeter
			if (instance instanceof GreeterFactory) {
				GreeterFactory factory = (GreeterFactory) instance;
				assertThat(factory.getGreeting(), is(GREETING));
				instances.decrementAndGet();
				factory.setGreeting(DESTROYED);
			}
		}
	};
	
	@BeforeMethod
	public void init() {
		instances = new AtomicInteger();
		factoryInitializer = new CountingPojoInitializer();
		lazyPojo = LazyPojo.forProvider(GreeterFactory.class, IPropertyResolver.empty(), factoryInitializer);
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
		assertEquals(instances.get(), 0);
		assertEquals(GREETING, lazyPojo.getInstance().sayGreeting());
		assertEquals(instances.get(), 1);
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

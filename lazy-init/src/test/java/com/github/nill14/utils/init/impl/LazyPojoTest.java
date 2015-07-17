package com.github.nill14.utils.init.impl;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.GreeterFactory;
import com.github.nill14.utils.init.IGreeter;
import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;

@SuppressWarnings("serial")
//@Test(invocationCount = 10)
public class LazyPojoTest {
	
	private static final String DESTROYED = "destroyed";
	private static final String GREETING = "Hello World!";
	private static final Logger log = LoggerFactory.getLogger(LazyPojoTest.class);
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private volatile ILazyPojo<IGreeter> lazyPojo;

	private static final AtomicInteger instances = new AtomicInteger();
	private static final IPojoInitializer factoryInitializer = new CountingPojoInitializer(instances);
	private EmptyPropertyResolver resolver;
			
	static class CountingPojoInitializer implements IPojoInitializer {
		
		private final AtomicInteger instances;

		public CountingPojoInitializer(AtomicInteger instances) {
			this.instances = instances;
		}
		
		@Override
		public <T> void init(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance, CallerContext context) {
			//instance could be also IGreeter
			if (instance instanceof GreeterFactory) {
				GreeterFactory factory = (GreeterFactory) instance;
				assertThat(factory.getGreeting(), not(GREETING));
				factory.setGreeting(GREETING);
			
			} else if (instance instanceof IGreeter) {
				instances.incrementAndGet();
			}
		}
		
		@Override
		public <T> void destroy(IPropertyResolver resolver, IBeanDescriptor<T> beanDescriptor, Object instance) {
			//instance could be also IGreeter
			if (instance instanceof GreeterFactory) {
				GreeterFactory factory = (GreeterFactory) instance;
				assertThat(factory.getGreeting(), is(GREETING));
				factory.setGreeting(DESTROYED);

			} else if (instance instanceof IGreeter) {
				instances.decrementAndGet();
			}
		}
	};
	
	@BeforeMethod
	public void init() {
		resolver = EmptyPropertyResolver.empty();
		resolver.appendInitializer(factoryInitializer);
		instances.set(0);
		
		lazyPojo = LazyPojo.forProvider(GreeterFactory.class, resolver);
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
	
//	@Test
	public void stressTest() throws InterruptedException, ExecutionException {
		
		ExecutorService ex = Executors.newFixedThreadPool(8);
		Random rnd = new Random();
		for (int i = 0; i < 10; ) {
			ex.submit(new Runnable() {
				
				@Override
				public void run() {
					if (rnd.nextBoolean()) {
						lazyPojo.init(executor);
					
					} else {
						lazyPojo.destroy(executor);
					}
				}
			});
		}

		ex.shutdown();
		ex.awaitTermination(1, TimeUnit.SECONDS);
		
		int actual = instances.get();
		log.info("actual (stress): {}", actual);
		assertThat(actual, anyOf(is(0), is(1)));
		assertEquals(GREETING, lazyPojo.getInstance().sayGreeting());
		assertEquals(1, instances.get());
	}
}

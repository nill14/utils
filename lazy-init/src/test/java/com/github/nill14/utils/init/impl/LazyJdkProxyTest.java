package com.github.nill14.utils.init.impl;
import static org.testng.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.Calculator;
import com.github.nill14.utils.init.ICalculator;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class LazyJdkProxyTest {

	private static final Logger log = LoggerFactory.getLogger(LazyJdkProxyTest.class);
	private static ICalculator calcProxy;
	private static ILazyPojo<Calculator> lazyObject;
	private static AtomicInteger instances = new AtomicInteger();

	private static final IPojoInitializer initializer = new IPojoInitializer() {
		@Override
		public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
			instances.incrementAndGet();
		}

		@Override
		public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) {
			instances.decrementAndGet();
		}
	};
	
	@BeforeClass
	public static void setUp() {
		lazyObject = LazyPojo.forBean(Calculator.class, IPropertyResolver.empty(), initializer);
		calcProxy = LazyJdkProxy.newProxy(ICalculator.class, lazyObject);
	}
	
	@BeforeMethod
	public void init() {
		lazyObject.freeInstance();
		assertEquals(0, instances.get());
	}

	@Test
	public void testAdd() {
		assertEquals(8, calcProxy.add(5, 3));
		assertEquals(1, instances.get());
	}
	
	@Test
	public void testHashCode() {
		log.info("hashCode: " + calcProxy.hashCode());
		assertEquals(0, instances.get());
	}

	@Test
	public void testToString() {
		log.info("toString: {}", calcProxy.toString());
		assertEquals(0, instances.get());
	}
	


	@Test
	public void testEquals() {
		assertTrue(calcProxy.equals(calcProxy));
		assertEquals(0, instances.get());
	}
	
	@Test
	public void testHexString() {
		for (int i = 0; i < 10; i++) {
			String hexString = Integer.toHexString(System.identityHashCode(new Object()));
			log.info("hexString: {}", hexString);
		}
	}


}

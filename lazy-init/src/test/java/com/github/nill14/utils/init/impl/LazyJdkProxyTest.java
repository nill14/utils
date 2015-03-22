package com.github.nill14.utils.init.impl;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nill14.utils.init.Calculator;
import com.github.nill14.utils.init.ICalculator;
import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class LazyJdkProxyTest {

	private static final Logger log = LoggerFactory.getLogger(LazyJdkProxyTest.class);
	private static ICalculator calcProxy;
	private static ILazyPojo<Calculator> lazyObject;
	private static AtomicInteger instances = new AtomicInteger();

	private static final IPojoInitializer<ICalculator> initializer = new IPojoInitializer<ICalculator>() {
		@Override
		public void init(Provider<?> factory, ICalculator instance) {
			instances.incrementAndGet();
		}
		
		@Override
		public void destroy(Provider<?> factory, ICalculator instance) {
			instances.decrementAndGet();
		}
	};
	
	@BeforeClass
	public static void setUp() {
		lazyObject = LazyPojo.forClass(Calculator.class, IPropertyResolver.empty(), initializer);
		calcProxy = LazyJdkProxy.newProxy(ICalculator.class, lazyObject);
	}
	
	@Before
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

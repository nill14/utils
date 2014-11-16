package com.github.nill14.utils.init.impl;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nill14.utils.init.Calc;
import com.github.nill14.utils.init.ICalc;
import com.github.nill14.utils.init.api.ILazyObject;
import com.github.nill14.utils.init.api.IObjectFactory;
import com.github.nill14.utils.init.api.IObjectInitializer;

public class LazyJdkProxyTest {

	private static final Logger log = LoggerFactory.getLogger(LazyJdkProxyTest.class);
	private static ICalc calcProxy;
	private static ILazyObject<Calc> lazyObject;
	private static AtomicInteger instances = new AtomicInteger();

	private static final IObjectInitializer<ICalc> initializer = new IObjectInitializer<ICalc>() {
		public void init(ICalc instance) {
			instances.incrementAndGet();
		}
		
		public void destroy(ICalc instance) {
			instances.decrementAndGet();
		}
	};
	
	@BeforeClass
	public static void setUp() {
		IObjectFactory<Calc> factory = BeanObjectFactory.create(Calc.class);
		lazyObject = new LazyObject<>(factory, initializer);
		calcProxy = LazyJdkProxy.newProxy(ICalc.class, lazyObject);
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

package com.github.nill14.utils.init.impl;

import static org.testng.Assert.*;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPropertyResolver;

public class WireTest {

	private static final Logger log = LoggerFactory.getLogger(WireTest.class);
	private final IPropertyResolver resolver = IPropertyResolver.empty();
	
	@Test
	public void testStrawberry() {
		IBeanInjector injector = new BeanInjector(resolver);
		Strawberry strawberry = injector.getInstance(Strawberry.class);
		assertNotNull(strawberry);
	}
	
	@Test
	public void testOnion() {
		IBeanInjector injector = new BeanInjector(resolver);
		Onion onion = injector.getInstance(Onion.class);
		assertNotNull(onion.strawberry);
	}
	
	@Test
	public void testMango() {
		IBeanInjector injector = new BeanInjector(resolver);
		Mango mango = injector.getInstance(Mango.class);
		assertNotNull(mango.onion);
	}
	
	public static class Mango {
		@Inject
		Onion onion;
	}
	
	public static class Onion {
		@Inject
		Strawberry strawberry;
	}
	
	public static class Strawberry {
		final String hello = "hello";
	}

}

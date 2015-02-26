package com.github.nill14.utils.moduledi.spring;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.github.nill14.utils.init.meta.Wire;
import com.google.common.collect.ImmutableMap;

public class SpringPropertyResolverTest {
	
	private static final Logger log = LoggerFactory.getLogger(SpringPropertyResolverTest.class);
	private ApplicationContext context;
	private MangoHello helloMango;
	
	@Before
	public void prepare() {
		context = mock(ApplicationContext.class);
		
		helloMango = new MangoHello();
		when(context.getBeansWithAnnotation(AnotherNamed.class))
			.thenReturn(ImmutableMap.of("hello", helloMango));
		
		when(context.getBeansOfType(Mango.class))
			.thenReturn(ImmutableMap.of("hello", helloMango));
	}
	
	@Test
	public void testMangoBean() throws NoSuchFieldException, SecurityException {
		SpringPropertyResolver resolver = new SpringPropertyResolver(context);
	
		Field field = MangoBean.class.getDeclaredField("helloMango");
		AnotherNamed annotation = field.getAnnotation(AnotherNamed.class);
		Named named = field.getAnnotation(Named.class);
		Iterator<Named> iterator = Collections.singleton(named).iterator();

		Object result = resolver.findByQualifier("pojo", Mango.class, annotation, iterator);
		assertEquals(helloMango, result);
	}
	
	@Test
	public void testMangoBean2() throws NoSuchFieldException, SecurityException {
		SpringPropertyResolver resolver = new SpringPropertyResolver(context);
		
	
		Field field = MangoBean.class.getDeclaredField("missingMango");
		AnotherNamed annotation = field.getAnnotation(AnotherNamed.class);
		
		Object result = resolver.findByQualifier("pojo", Mango.class, annotation, Collections.emptyIterator());
		assertEquals(null, result);
	}
	
	public abstract static class Mango {
		@Inject
		@Wire
		Onion onion;
	}
	
	public static class Onion {
	}
	
	
	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Synchronous {
		
	}
	
	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Asynchronous {
		
	}
	
	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface AnotherNamed {
		String value();
	}
	
	@Synchronous
	public static class MangoSync extends Mango {
		
	}
	
	@Asynchronous
	public static class MangoAsync extends Mango {
		
	}
	
	@AnotherNamed("hello")
	@Named("helloMango")
	public static class MangoHello extends Mango {
		
	}
	
	public static class MangoBean {
		
		@Inject
		@Synchronous
		@Nullable
		Mango syncMango;
		
		@Inject
		@Asynchronous
		@Nullable
		Mango asyncMango;
		
		@Inject
		@AnotherNamed("hello")
		@Named("helloMango")
		@Nullable
		Mango helloMango;
		
		@Inject
		@AnotherNamed("missing")
		@Nullable
		Mango missingMango;
	}
}

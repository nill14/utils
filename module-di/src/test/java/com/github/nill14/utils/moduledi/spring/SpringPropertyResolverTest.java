package com.github.nill14.utils.moduledi.spring;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IParameterType;
import com.google.common.collect.ImmutableMap;

public class SpringPropertyResolverTest {
	
	private static final Logger log = LoggerFactory.getLogger(SpringPropertyResolverTest.class);
	private ApplicationContext context;
	private MangoHello helloMango;
	
	@BeforeMethod
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
		
		Object result = resolver.findByQualifier(IParameterType.of(Mango.class), annotation);
		Assert.assertEquals(helloMango, result);
	}
	
	@Test
	public void testMangoBean2() throws NoSuchFieldException, SecurityException {
		SpringPropertyResolver resolver = new SpringPropertyResolver(context);
		
	
		Field field = MangoBean.class.getDeclaredField("missingMango");
		AnotherNamed annotation = field.getAnnotation(AnotherNamed.class);
		
		Object result = resolver.findByQualifier(IParameterType.of(Mango.class), annotation);
		assertEquals(result, null);
	}
	
	public abstract static class Mango {
		@Inject
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

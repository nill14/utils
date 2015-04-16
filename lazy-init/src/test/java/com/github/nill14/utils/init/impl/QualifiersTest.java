package com.github.nill14.utils.init.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.hamcrest.CoreMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;

public class QualifiersTest {

	private static final Logger log = LoggerFactory.getLogger(QualifiersTest.class);
	private IBeanInjector beanInjector;
	
	@BeforeMethod
	public void prepare() {
		IServiceRegistry serviceRegistry = IServiceRegistry.newRegistry();
		serviceRegistry.addService(MangoSync.class, IServiceContext.global());
		serviceRegistry.addService(MangoAbc.class, IServiceContext.global());
		serviceRegistry.addService(MangoHello.class, IServiceContext.global());
		beanInjector = serviceRegistry.toBeanInjector();
	}
	
	@Test
	public void testMangoBean() {
		MangoBean bean = beanInjector.wire(MangoBean.class);
		
		assertNotNull(bean);
		assertThat(bean.abcMango, CoreMatchers.instanceOf(MangoAbc.class));
		assertThat(bean.syncMango, CoreMatchers.instanceOf(MangoSync.class));
		assertThat(bean.helloMango, CoreMatchers.instanceOf(MangoHello.class));
		assertNull(bean.missingMango);
	}
	
	public abstract static class Mango {
		@Inject
		Onion onion;
	}
	
	public static class Onion {
	}
	
	public enum ABC {
		A, B, C
	}

	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface AbcQualifier {
		ABC value();
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
	
	@AbcQualifier(ABC.A)
	public static class MangoAbc extends Mango {
		
	}
	
	@Named("helloMango")
	public static class MangoHello extends Mango {
		
	}
	
	public static class MangoBean {
		
		@Inject
		@Synchronous
		@Nullable
		Mango syncMango;
		
		@Inject
		@AbcQualifier(ABC.A)
		@Nullable
		Mango abcMango;
		
		@Inject
		@Named("helloMango")
		@Nullable
		Mango helloMango;
		
		@Inject
		@AnotherNamed("missing")
		@Nullable
		Mango missingMango;
	}
}

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
import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public class QualifiedProviderTest {

	private static final Logger log = LoggerFactory.getLogger(QualifiersTest.class);
	private IBeanInjector beanInjector;
	
	@BeforeMethod
	public void prepare() {
		IServiceRegistry serviceRegistry = IServiceRegistry.newRegistry();
		serviceRegistry.addService(MangoSync.class, IServiceContext.global());
		serviceRegistry.addService(MangoAsync.class, IServiceContext.global());
		serviceRegistry.addService(MangoHello.class, IServiceContext.global());
		QualifiedProvider<Mango> mangoQualifiedProvider = new QualifiedProvider<Mango>(new TypeToken<Mango>() {}, serviceRegistry.toResolver());
		serviceRegistry.addSingleton(mangoQualifiedProvider);
		beanInjector = serviceRegistry.toBeanInjector(CallerContext.prototype());
	}

	@Test
	public void testTypeToken() throws NoSuchFieldException, SecurityException {
		QualifiedProvider<Mango> mangoProvider = beanInjector.getInstance(new TypeToken<QualifiedProvider<Mango>>() {});
		assertNotNull(mangoProvider);
	}
	
	@Test
	public void testMangoBeanAnnotated() throws NoSuchFieldException, SecurityException {
		QualifiedProvider<Mango> mangoProvider = beanInjector.getInstance(new TypeToken<QualifiedProvider<Mango>>() {});
		
		AbcQualifier abcQualifier = MangoBean.class.getDeclaredField("helloMango").getAnnotation(AbcQualifier.class);
		assertThat(mangoProvider.getQualified(abcQualifier), CoreMatchers.instanceOf(MangoAsync.class));
	}
	
	@Test
	public void testMangoBeanAnnotatedType() throws NoSuchFieldException, SecurityException {
		QualifiedProvider<Mango> mangoProvider = beanInjector.getInstance(new TypeToken<QualifiedProvider<Mango>>() {});
		
		assertThat(mangoProvider.getQualified(Synchronous.class), CoreMatchers.instanceOf(MangoSync.class));
	}

	@Test
	public void testMangoBeanNamed() throws NoSuchFieldException, SecurityException {
		QualifiedProvider<Mango> mangoProvider = beanInjector.getInstance(new TypeToken<QualifiedProvider<Mango>>() {});
		//FIXME make sure that guice Named and javax Named can resolve to each other.
		assertThat(mangoProvider.getNamed("helloMango"), CoreMatchers.instanceOf(MangoHello.class));
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testMangoBeanFail() {
		QualifiedProvider<Mango> mangoProvider = beanInjector.getInstance(new TypeToken<QualifiedProvider<Mango>>() {});
		
		mangoProvider.getQualified(AbcQualifier.class);
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
	public static class MangoAsync extends Mango {
		
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
		Mango asyncMango;
		
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

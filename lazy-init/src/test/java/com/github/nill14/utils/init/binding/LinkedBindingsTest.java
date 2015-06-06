package com.github.nill14.utils.init.binding;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Scope;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.scope.AbstractThreadScope;

public class LinkedBindingsTest {

	private static final AtomicInteger instances = new AtomicInteger();
	private IBeanInjector beanInjector;
	private CustomScopeImpl scope;

	@BeforeMethod public void beforeMethod() {
	}

	@BeforeMethod
	public void beforeClass() {
		instances.set(0);
		TestBinder b = new TestBinder();
		
		b.bindScope(CustomScope.class, scope = new CustomScopeImpl());
		
		b.bind(Bean2.class).in(CustomScope.class);
		b.bind(Bean.class).to(Bean2.class).in(CustomScope.class);
		b.bind(IFace1.class).to(Bean.class).in(CustomScope.class);
		b.bind(IFace2.class).to(Bean.class).in(CustomScope.class);
		
		beanInjector = b.toBeanInjector();
	}

	@Test
	public void testInstances() {
		IFace1 iface1 = beanInjector.getInstance(IFace1.class);
		IFace2 iface2 = beanInjector.getInstance(IFace2.class);
		Bean bean = beanInjector.getInstance(Bean.class);
		Bean2 bean2 = beanInjector.getInstance(Bean2.class);
		
		
		Assert.assertEquals(bean, bean2);
		Assert.assertEquals(iface1, iface2);
		Assert.assertEquals(instances.get(), 1);
		
		scope.destroy();
		Assert.assertEquals(instances.get(), 0);
	}

	
	
	private interface IFace1 {
		
	}
	
	private interface IFace2 {
		
	}
	
	private class Bean implements IFace1, IFace2 {
		final int num;

		@Inject
		public Bean() {
			num = instances.incrementAndGet();
		}
		
		@PreDestroy
		public void destroy() {
			instances.decrementAndGet();
		}
		@Override
		public String toString() {
			return String.format("Bean-%d", num);
		}
	}
	
	
	private class Bean2 extends Bean {
		@Inject
		public Bean2() {
			super();
		}
		
		@Override
		public String toString() {
			return String.format("Bean2-%d", num);
		}
	}	

	@Scope
	@Retention(RUNTIME)
	private static @interface CustomScope {
		
	}
	
	private static class CustomScopeImpl extends AbstractThreadScope {
		
		public void destroy() {
			get().terminate();
		}
		

		@Override
		public String toString() {
			return "CustomScope";
		}
		
	}
	


}

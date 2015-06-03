package com.github.nill14.utils.init.binding;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.meta.Annotations;

public class SingletonScopeTest {

	private final AtomicInteger instances = new AtomicInteger();
	private IBeanInjector beanInjector;

	@BeforeMethod public void beforeMethod() {
	}

	@BeforeClass
	public void beforeClass() {
		TestBinder b = new TestBinder();
		
		b.bind(IBean.class)
			.annotatedWith(Annotations.named("qualified"))
			.to(Bean.class)
			.in(Singleton.class);
		
		b.bind(IBean.class)
			.annotatedWith(Annotations.named("named"))
			.to(Bean.class)
			.in(Singleton.class);
		
		b.bind(IBean.class)
			.to(Bean.class)
			.in(Singleton.class);
		
		beanInjector = b.toBeanInjector();
	}

	@Test
	public void testInstances() {
		IBean qualified = beanInjector.getInstance(BindingKey.of(IBean.class, Annotations.named("qualified")));
		IBean named = beanInjector.getInstance(BindingKey.of(IBean.class, Annotations.named("named")));
		IBean bean = beanInjector.getInstance(IBean.class);
		
		
		Assert.assertNotEquals(qualified, named);
		Assert.assertTrue(instances.get() <= 3);
	}

	
	private interface IBean {
		
	}
	
	private class Bean implements IBean {
		public Bean() {
			instances.incrementAndGet();
		}
	}

}

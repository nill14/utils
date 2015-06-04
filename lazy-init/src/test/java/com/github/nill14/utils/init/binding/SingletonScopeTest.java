package com.github.nill14.utils.init.binding;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.meta.Annotations;
import com.google.common.reflect.TypeToken;

public class SingletonScopeTest {

	private final AtomicInteger instances = new AtomicInteger();
	private IBeanInjector beanInjector;

	@BeforeMethod public void beforeMethod() {
	}

	@BeforeMethod
	public void before() {
		instances.set(0);
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
		
		b.bind(IBean.class)
			.to(Bean2.class)
			.in(Singleton.class);		
		
		b.bind(IBean.class)
			.to(Bean2.class);
		
		beanInjector = b.toBeanInjector();
	}

	@Test
	public void testInstances() {
		IBean qualified = beanInjector.getInstance(BindingKey.of(IBean.class, Annotations.named("qualified")));
		IBean named = beanInjector.getInstance(BindingKey.of(IBean.class, Annotations.named("named")));
		IBean bean = beanInjector.getInstance(IBean.class);

		
		Assert.assertNotEquals(qualified, named);
		Assert.assertNotEquals(qualified, bean);
		Assert.assertEquals(instances.get(), 3);
	}



	@Test
	public void testCollections() {
		IBean qualified = beanInjector.getInstance(BindingKey.of(IBean.class, Annotations.named("qualified")));
		IBean named = beanInjector.getInstance(BindingKey.of(IBean.class, Annotations.named("named")));
		IBean bean = beanInjector.getInstance(IBean.class);
		
		List<IBean> beans = beanInjector.getInstance(new TypeToken<List<IBean>>() {});
		
		Assert.assertNotEquals(qualified, named);
		Assert.assertNotEquals(qualified, bean);
		Assert.assertEquals(instances.get(), 3);
		Assert.assertEquals(beans.size(), 5);
	}
	
	private interface IBean {
		
	}
	
	private class Bean implements IBean {
		@Inject
		public Bean() {
			instances.incrementAndGet();
		}
	}
	
	private class Bean2 implements IBean {
		@Inject
		public Bean2() {
			instances.incrementAndGet();
		}
	}


}

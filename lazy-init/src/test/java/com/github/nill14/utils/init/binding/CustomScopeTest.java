package com.github.nill14.utils.init.binding;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Scope;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.meta.Annotations;
import com.github.nill14.utils.init.scope.AbstractThreadScope;
import com.google.common.reflect.TypeToken;

public class CustomScopeTest {

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
		
		b.bind(IBean.class)
			.annotatedWith(Annotations.named("qualified"))
			.to(Bean.class)
			.in(CustomScope.class);
		
		b.bind(IBean.class)
			.annotatedWith(Annotations.named("named"))
			.to(Bean.class)
			.in(CustomScope.class);
		
		b.bind(IBean.class)
			.to(Bean.class)
			.in(CustomScope.class);
	
		b.bind(IBean.class)
			.to(Bean2.class)
			.in(CustomScope.class);		
		
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
		
		scope.destroy();
		Assert.assertEquals(instances.get(), 0);
	}

	
	@Test
	public void testCollections() {
		List<IBean> beans = beanInjector.getInstance(new TypeToken<List<IBean>>() {});

		IBean qualified = beanInjector.getInstance(BindingKey.of(IBean.class, Annotations.named("qualified")));
		IBean named = beanInjector.getInstance(BindingKey.of(IBean.class, Annotations.named("named")));
		IBean bean = beanInjector.getInstance(IBean.class);
		
		Assert.assertNotEquals(qualified, named);
		Assert.assertNotEquals(qualified, bean);
		Assert.assertEquals(instances.get(), 5);
		Assert.assertEquals(beans.size(), 5);
		Assert.assertEquals(instances.get(), 5);
	}
	
	private interface IBean {
		
	}
	
	private class Bean implements IBean {
		private final int num;

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
	
	
	private class Bean2 implements IBean {
		private final int num;

		@Inject
		public Bean2() {
			num = instances.incrementAndGet();
		}
		
		@PreDestroy
		public void destroy() {
			instances.decrementAndGet();
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

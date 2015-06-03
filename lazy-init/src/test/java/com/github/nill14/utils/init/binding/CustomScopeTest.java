package com.github.nill14.utils.init.binding;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Scope;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.meta.Annotations;
import com.github.nill14.utils.init.scope.AbstractThreadScope;

public class CustomScopeTest {

	private static final AtomicInteger instances = new AtomicInteger();
	private IBeanInjector beanInjector;
	private CustomScopeImpl scope = new CustomScopeImpl();

	@BeforeMethod public void beforeMethod() {
	}

	@BeforeClass
	public void beforeClass() {
		TestBinder b = new TestBinder();
		
		b.bindScope(CustomScope.class, scope);
		
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

	
	private interface IBean {
		
	}
	
	private class Bean implements IBean {
		@Inject
		public Bean() {
			instances.incrementAndGet();
		}
		
		@PreDestroy
		public void destroy() {
			instances.decrementAndGet();
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
		
	}

}

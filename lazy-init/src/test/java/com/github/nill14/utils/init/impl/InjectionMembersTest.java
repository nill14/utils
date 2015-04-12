package com.github.nill14.utils.init.impl;

import static org.testng.Assert.*;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.TestBinder;

@SuppressWarnings("serial")
public class InjectionMembersTest {

	private static final Logger log = LoggerFactory.getLogger(QualifiersTest.class);

	@Test
	public void testMethodInject() {
		TestBinder b = new TestBinder();
		b.bind(Dep.class);
		IBeanInjector beanInjector = b.toBeanInjector();
		BeanWithMethodInject bean = beanInjector.wire(BeanWithMethodInject.class);
		assertNotNull(bean.getDependency());
	}
	
	@Test
	public void testConstructorInject() {
		TestBinder b = new TestBinder();
		b.bind(Dep.class);
		IBeanInjector beanInjector = b.toBeanInjector();
		BeanWithConstructor bean = beanInjector.wire(BeanWithConstructor.class);
		assertNotNull(bean.getDependency());
	}
	
	@Test
	public void testTwoConstructors() {
		TestBinder b = new TestBinder();
		b.bind(Dep.class);
		IBeanInjector beanInjector = b.toBeanInjector();
		BeanWithTwoConstructors bean = beanInjector.wire(BeanWithTwoConstructors.class);
	}
	
	public static class Dep {
		
	}
	
	public static class BeanWithConstructor {
		
		private final Dep dependency;

		@Inject
		public BeanWithConstructor(Dep dependency) {
			this.dependency = dependency;
		}
		
		public Dep getDependency() {
			return dependency;
		}
	}
	
	public static class BeanWithTwoConstructors {
		
		@Inject
		public BeanWithTwoConstructors(Dep dep) {
			
		}
		
		public BeanWithTwoConstructors() {
			
		}
	}
	
	public static class BeanWithTwoArgConstructors {
		
		@Inject
		public BeanWithTwoArgConstructors(Dep dep) {
			
		}
		
		public BeanWithTwoArgConstructors(Dep dep, @Nullable String str) {
			
		}
	}
	
	public static class BeanWithMethodInject {
		
		private Dep dependency;
		
		
		public Dep getDependency() {
			return dependency;
		}

		@Inject
		public void setDependency(Dep dependency) {
			this.dependency = dependency;
		}
	}
}

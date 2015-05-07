package com.github.nill14.utils.moduledi;

import java.util.concurrent.Semaphore;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.Calculator;
import com.github.nill14.utils.init.ICalculator;
import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.AnnotatedBindingBuilder;
import com.github.nill14.utils.init.binding.Binder;
import com.github.nill14.utils.init.impl.QualifiedProviderTest.Synchronous;
import com.github.nill14.utils.moduledi2.IModule;
import com.github.nill14.utils.moduledi2.ModuleDI;

public class ModulesTest {

	
	@Test(timeOut=1500)
	public void testConfigureCalled() throws InterruptedException {
		Semaphore s = new Semaphore(0);
		ModuleDI.createBeanInjector(new IModule() {

			@Override
			public void activate(IBeanInjector beanInjector) {
			}

			@Override
			public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
				
			}

			@Override
			public void configure(Binder binder) {
				s.release();
			}
		});
		s.acquire();
	}
	
	@Test(timeOut=1500)
	public void testActivateCalled() throws InterruptedException {
		Semaphore s = new Semaphore(0);
		ModuleDI.createBeanInjector(new IModule() {

			@Override
			public void activate(IBeanInjector beanInjector) {
				s.release();
			}

			@Override
			public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
				
			}

			@Override
			public void configure(Binder binder) {
			}
		});
		s.acquire();
	}
	
	@Test(expectedExceptions = RuntimeException.class)
	public void testBindAfter() {
		ModuleDI.createBeanInjector(new IModule() {

			private Binder binder;

			@Override
			public void activate(IBeanInjector beanInjector) {
				//this should fail
				binder.bind(ICalculator.class).to(Calculator.class);
				
			}

			@Override
			public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
			}

			@Override
			public void configure(Binder binder) {
				this.binder = binder;
			}
		});
	}
	
	@Test(expectedExceptions = RuntimeException.class)
	public void testBindAfter2() {
		ModuleDI.createBeanInjector(new IModule() {

			private AnnotatedBindingBuilder<ICalculator> bind;

			@Override
			public void activate(IBeanInjector beanInjector) {
				//this should fail
				bind.to(Calculator.class);
				
			}

			@Override
			public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
			}

			@Override
			public void configure(Binder binder) {
				bind = binder.bind(ICalculator.class);
			}
		});
	}


	@Test
	public void testCalc() {
		IBeanInjector beanInjector = ModuleDI.createBeanInjector(new IModule() {

			@Override
			public void activate(IBeanInjector beanInjector) {
				
			}

			@Override
			public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
			}

			@Override
			public void configure(Binder binder) {
				binder.bind(ICalculator.class).to(Calculator.class);
			}
		});
		
		ICalculator calc = beanInjector.getInstance(ICalculator.class);
		MatcherAssert.assertThat(calc, CoreMatchers.instanceOf(ICalculator.class));
	}
	
	@Test
	public void testAnnotatedCalc() {
		IBeanInjector beanInjector = ModuleDI.createBeanInjector(new IModule() {

			@Override
			public void activate(IBeanInjector beanInjector) {
				
			}

			@Override
			public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
			}

			@Override
			public void configure(Binder binder) {
				binder.bind(ICalculator.class).annotatedWith(Synchronous.class).to(Calculator.class);
			}
		});
		
		ICalculator calc = beanInjector.getInstance(BindingKey.of(ICalculator.class, Synchronous.class));
		MatcherAssert.assertThat(calc, CoreMatchers.instanceOf(ICalculator.class));
	}
	
	
	static class ModuleA implements IModule {

		@Override
		public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void configure(Binder binder) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void activate(IBeanInjector beanInjector) {
			// TODO Auto-generated method stub
			System.out.println("activate");
		}
		
	}
}

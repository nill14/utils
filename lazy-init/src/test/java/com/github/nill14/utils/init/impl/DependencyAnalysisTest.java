package com.github.nill14.utils.init.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.api.IQualifiedProvider;
import com.github.nill14.utils.init.binding.TestBinder;
import com.github.nill14.utils.init.meta.Provides;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

public class DependencyAnalysisTest {

	private SimplePropertyResolver resolver;
	private Set<Class<?>> requiredDependencies;
	private Set<Class<?>> optionalDependencies;
	
	@BeforeMethod
	public void prepare() {
		TestBinder b = new TestBinder();
		b.bind(Bean.class);
		b.bind(Bean2.class);
		
		b.scanProvidesBindings(this);
		
		resolver = (SimplePropertyResolver) b.toResolver();
		Map<TypeToken<?>, Boolean> collectDependencies = resolver.collectDependencies();
		requiredDependencies = Sets.newHashSet();
		optionalDependencies = Sets.newHashSet();
		
		for (Entry<TypeToken<?>, Boolean> dep : collectDependencies.entrySet()) {
			boolean isRequired = dep.getValue();
			TypeToken<?> token = dep.getKey();
			if (isRequired) {
				requiredDependencies.add(token.getRawType());
			} else {
				optionalDependencies.add(token.getRawType());
			}
		}
	}
	
	@Test
	public void testDependencies() {
		
		Assert.assertFalse(optionalDependencies.contains(Set.class));
		Assert.assertFalse(requiredDependencies.contains(Set.class));
		
		Assert.assertFalse(optionalDependencies.contains(TreeSet.class));
		Assert.assertFalse(requiredDependencies.contains(TreeSet.class));		
		
		Assert.assertFalse(optionalDependencies.contains(List.class));
		Assert.assertFalse(requiredDependencies.contains(List.class));
		
		Assert.assertFalse(optionalDependencies.contains(Optional.class));
		Assert.assertFalse(requiredDependencies.contains(Optional.class));
		
		Assert.assertFalse(optionalDependencies.contains(IBeanInjector.class));
		Assert.assertFalse(requiredDependencies.contains(IBeanInjector.class));
		
		Assert.assertFalse(optionalDependencies.contains(Provider.class));
		Assert.assertFalse(requiredDependencies.contains(Provider.class));
		
		Assert.assertFalse(optionalDependencies.contains(IQualifiedProvider.class));
		Assert.assertFalse(requiredDependencies.contains(IQualifiedProvider.class));

		Assert.assertTrue(requiredDependencies.contains(Onion.class));
		Assert.assertTrue(requiredDependencies.contains(Wallnut.class));
		Assert.assertTrue(requiredDependencies.contains(Mango.class));

		Assert.assertTrue(optionalDependencies.contains(Cucumber.class));
		Assert.assertTrue(optionalDependencies.contains(Tomato.class));
		Assert.assertTrue(optionalDependencies.contains(Potato.class));
		Assert.assertTrue(optionalDependencies.contains(Peanut.class));
		Assert.assertTrue(optionalDependencies.contains(Seed.class));
	}
	
	static class Bean {
		@Inject
		Onion onion;
		
		@Inject 
		Set<Cucumber> cucumbers;
		
		@Inject 
		@Nullable
		Tomato optTomato;
		
		@Inject
		Optional<Potato> optPotato;
		
		@Inject List<Wallnut> wallnuts;
		
		
	}
	
	static class Bean2 {
		@Inject
		@Nullable
		Onion onion;
		
		@Inject Wallnut wallnut;
		
		@Inject
		Optional<Potato> optPotato;
		
		@Inject
		IBeanInjector beanInjector;
		
		@Inject
		Provider<TreeSet<Peanut>> potatoProvider;
		
		@Inject
		IQualifiedProvider<Tomato> qualifiedTomatos;
	}	
	
	@Provides
	public Tomato provideTomatos(Mango mango) {
		return new Tomato();
	}
	
	static class Onion {
	}
	
	static class Cucumber {
		@Inject
		Seed seed;
	}
	
	static class Tomato {
	}
	
	static class Potato implements Comparable<Potato> {

		@Override
		public int compareTo(Potato o) {
			return 0;
		}
	}
	
	static class Wallnut {
		
	}
	
	static class Peanut {
		
	}
	
	static class Seed {
		
	}
	
	static class Mango {
		
	}
}

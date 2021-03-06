package com.github.nill14.utils.init.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.util.List;

import javax.inject.Provider;

import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public class PojoFactoriesTest {
	
	
	static interface IFace<T> {
		
	}
	
	static class Impl implements IFace<List<String>> {
		
	}
	
	static class ImplProvider implements Provider<Impl> {

		@Override
		public Impl get() {
			return new Impl();
		}
	}; 	
	
	private CallerContext constructionContext() {
		return CallerContext.prototype();
	}

	@Test
	public void nullFactoryTest() {
		IPojoFactory<Integer> factory = BeanInstancePojoFactory.nullFactory(Integer.class);
		assertEquals(TypeToken.of(Integer.class), factory.getType());
		assertEquals(null, factory.newInstance(IPropertyResolver.empty(), constructionContext()));
	}
	
	@Test
	public void singletonFactoryTest() {
		Impl singleton = new Impl();
		IPojoFactory<IFace<List<String>>> factory = BeanInstancePojoFactory.singleton(singleton);
		assertEquals(TypeToken.of(Impl.class), factory.getType());
		assertEquals(singleton, factory.newInstance(IPropertyResolver.empty(), constructionContext()));
	}
	
	@Test
	public void providerFactoryTest() {
		Impl singleton = new Impl();
		TypeToken<IFace<List<String>>> expectedToken = new TypeToken<IFace<List<String>>>() {};
		Provider<IFace<List<String>>> provider = new Provider<PojoFactoriesTest.IFace<List<String>>>() {

			@Override
			public IFace<List<String>> get() {
				return singleton;
			}
		};
		IPojoFactory<IFace<List<String>>> factory = new ProviderInstancePojoFactory<>(provider);
		assertEquals(expectedToken, factory.getType());
		assertEquals(singleton, factory.newInstance( IPropertyResolver.empty(), constructionContext()));
	}
	
	@Test
	public void beanClassFactoryTest() {
		TypeToken<Impl> typeToken = TypeToken.of(Impl.class);
		IPojoFactory<Impl> factory = new BeanTypePojoFactory<>(typeToken);
		assertEquals(typeToken, factory.getType());
		assertThat(factory.newInstance(IPropertyResolver.empty(), constructionContext()), CoreMatchers.instanceOf(Impl.class));
	}
	
	@Test
	public void factoryAdapterTest() {
		TypeToken<Impl> typeToken = TypeToken.of(Impl.class);
		TypeToken<ImplProvider> providerToken = TypeToken.of(ImplProvider.class);
		ProviderTypePojoFactory<Impl, ImplProvider> adapter = new ProviderTypePojoFactory<>(providerToken);
		IPojoFactory<Impl> factory = adapter;
		assertEquals(typeToken, factory.getType());
		assertThat(factory.newInstance( IPropertyResolver.empty(), constructionContext()), CoreMatchers.instanceOf(Impl.class));
		assertEquals(providerToken, adapter.getFactoryType());
	}

}

package com.github.nill14.utils.init.binding;

import static org.hamcrest.MatcherAssert.*;

import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.Calculator;
import com.github.nill14.utils.init.ICalculator;
import com.github.nill14.utils.init.api.BindingType;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.impl.QualifiedProviderTest.Synchronous;
import com.google.common.reflect.TypeToken;

public class BindingBuilderTest {

	@Test
	public void test() {
		TestBinder b = new TestBinder();
//		b.bind(ICalculator.class).to(Calculator.class);
		b.bind(TypeToken.of(ICalculator.class)).annotatedWith(Synchronous.class).toProvider(Calculator::new);
		b.bind(Calculator.class);//.in(Synchronous.class);
		
		IBeanInjector beanInjector = b.toBeanInjector();
		Object object = beanInjector.wire(BindingType.of(ICalculator.class, Synchronous.class));
		
		assertThat(object, CoreMatchers.instanceOf(ICalculator.class));
	}

}

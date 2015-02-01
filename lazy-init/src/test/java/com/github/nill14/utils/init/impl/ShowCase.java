package com.github.nill14.utils.init.impl;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.mockito.Mockito;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;


interface ICalc {
	int add(int a, int b);
}

class Calc implements ICalc {
	@Override
	public int add(int a, int b) {
		return a + b;
	}
}

public class ShowCase {

	public static void main(String[] args) {

		//LazyJdkProxy example
		ICalc lazyProxy = LazyJdkProxy.newProxy(ICalc.class, Calc.class);
		lazyProxy.add(5, 3); //lazy initialization

		//LazyPojo example
		ILazyPojo<Calc> lazyPojo = LazyPojo.forClass(Calc.class);
		lazyPojo.getInstance().add(5, 3);  //lazy initialization
		
		// Complex example
		IPropertyResolver resolver = Mockito.mock(IPropertyResolver.class);
		ExecutorService executor = Executors.newCachedThreadPool();
		IPojoInitializer<Object> initializer = AnnotationPojoInitializer.withResolver(resolver);
		IPojoFactory<Calc> pojoFactory = PojoFactory.create(Calc.class);
		ILazyPojo<ICalc> calcPojo = new LazyPojo<>(pojoFactory, initializer);
		calcPojo.init(executor); //eagerly start asynchronous initialization
		ICalc calc = LazyJdkProxy.newProxy(ICalc.class, calcPojo);
		calc.add(5, 3); //invokes the initialized instance or blocks until it is ready.
	}
	
	void test() {
		IServiceRegistry registry = new ServiceRegistry();
		registry.addService("seedService", SeedService.class, IServiceContext.global());
		registry.addService("diceService", DiceService.class, IServiceContext.global());
		registry.getService(IDiceService.class).rollDice();
	}
	
}

interface IDiceService {
	int rollDice();
}

interface ISeedService {
	long getSeed();
}

class SeedService implements ISeedService {
	@Override
	public long getSeed() {
		return 42;
	}
}

class DiceService implements IDiceService {
	@Inject
	private ISeedService seedService;
	private Random rand;
	
	@PostConstruct
	public void init() {
		rand = new Random(seedService.getSeed());
	}
	
	@Override
	public int rollDice() {
		return rand.nextInt(6) + 1;
	}
	
	@PreDestroy
	public void destroy() {
		rand = null;
	}
}
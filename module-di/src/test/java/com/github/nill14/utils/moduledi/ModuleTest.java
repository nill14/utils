package com.github.nill14.utils.moduledi;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.annotations.Test;

import com.github.nill14.parsers.dependency.IDependencyGraph;
import com.github.nill14.parsers.dependency.UnsatisfiedDependencyException;
import com.github.nill14.parsers.dependency.impl.DependencyGraphFactory;
import com.github.nill14.parsers.dependency.impl.DependencyTreePrinter;
import com.github.nill14.parsers.dependency.impl.ModuleRankingsPrinter;
import com.github.nill14.parsers.graph.CyclicGraphException;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.java8.stream.GuavaCollectors;
import com.github.nill14.utils.moduledi.module.ActivationModule;
import com.github.nill14.utils.moduledi.module.BreadModule;
import com.github.nill14.utils.moduledi.module.CustomerModule;
import com.github.nill14.utils.moduledi.module.DeliveryModule;
import com.github.nill14.utils.moduledi.module.SnackModule;
import com.google.common.collect.ImmutableSet;

public class ModuleTest {

	@Test
	public void test() throws UnsatisfiedDependencyException, CyclicGraphException, ExecutionException {
		
		try {
			IServiceRegistry registry = IServiceRegistry.newRegistry();
			
			Set<IModule> modules = ImmutableSet.of(
					new BreadModule(), 
					new SnackModule(), 
					new DeliveryModule(), 
					new CustomerModule(),
					new ActivationModule()
			);
			
			Set<ExecutionUnit> units = modules.stream()
					.map(ExecutionUnit::new)
					.collect(GuavaCollectors.toImmutableSet());
			
			units.forEach(m -> m.buildServices(registry));
			
			IDependencyGraph<ExecutionUnit> dependencyGraph = 
					DependencyGraphFactory.newInstance(units, m -> m.getDependencyDescriptor());
			
			// prints out the dependency tree to System.out
			new DependencyTreePrinter<>(dependencyGraph).toConsole();
			new ModuleRankingsPrinter<>(dependencyGraph).toConsole();

			ExecutorService executor = Executors.newCachedThreadPool();
			dependencyGraph.walkGraph(executor, module -> {
				String name = Thread.currentThread().getName();
				try {
					Thread.currentThread().setName(module.getName());
					module.startModule(registry);
				} finally {
					Thread.currentThread().setName(name);
				}
			});
			
			
			
//	    ISnackService snackService = injector.getInstance(ISnackService.class);
//	    System.out.println(snackService);
			
		} catch (UnsatisfiedDependencyException | CyclicGraphException | ExecutionException e) {
//			e.printStackTrace();
			throw e;
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		new ModuleTest().test();
	}
}

package com.github.nill14.utils.moduledi;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.github.nill14.parsers.dependency.IDependencyGraph;
import com.github.nill14.parsers.dependency.UnsatisfiedDependencyException;
import com.github.nill14.parsers.dependency.impl.DependencyGraphFactory;
import com.github.nill14.parsers.dependency.impl.ModuleRankingsPrinter;
import com.github.nill14.parsers.graph.CyclicGraphException;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.ServiceRegistry;
import com.github.nill14.utils.moduledi.module.ActivationModule;
import com.github.nill14.utils.moduledi.module.CustomerModule;
import com.google.common.collect.ImmutableSet;

public class ModuleTest {

	@Test
	public void test() throws UnsatisfiedDependencyException, CyclicGraphException, ExecutionException {
		
		try {
			IServiceRegistry registry = new ServiceRegistry();
			
			Set<AbstractModule> modules = ImmutableSet.of(
//					new BreadModule(), 
//					new SnackModule(), 
//					new DeliveryModule(), 
					new CustomerModule(),
					new ActivationModule()
			);
			
			modules.forEach(m -> m.buildServices(registry));
			
			IDependencyGraph<AbstractModule> dependencyGraph = 
					DependencyGraphFactory.newInstance(modules, m -> m.getDependencyDescriptor());
			
			ExecutorService executor = Executors.newCachedThreadPool();
			// execute first ModuleA and ModuleC in parallel and when completed, executes ModuleB
			dependencyGraph.walkGraph(executor, module -> module.startModule(registry));
			
			// prints out the dependency tree to System.out
			new ModuleRankingsPrinter<>(dependencyGraph).toConsole();
			
			
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

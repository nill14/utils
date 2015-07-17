package com.github.nill14.utils.moduledi2;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.nill14.parsers.dependency.IConsumer;
import com.github.nill14.parsers.dependency.IDependencyGraph;
import com.github.nill14.parsers.dependency.UnsatisfiedDependencyException;
import com.github.nill14.parsers.dependency.impl.DependencyGraphFactory;
import com.github.nill14.parsers.dependency.impl.DependencyTreePrinter;
import com.github.nill14.parsers.dependency.impl.ModuleRankingsPrinter;
import com.github.nill14.parsers.graph.CyclicGraphException;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.ModuleBinder;
import com.github.nill14.utils.init.binding.TestBinder;
import com.github.nill14.utils.init.binding.impl.BindingImpl;
import com.github.nill14.utils.init.impl.CallerContext;
import com.github.nill14.utils.init.impl.ServiceRegistry;
import com.github.nill14.utils.java8.stream.ExecutorUtils;
import com.github.nill14.utils.java8.stream.GuavaCollectors;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public final class ModularBeanInjectorBuilder {
	
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final ServiceRegistry serviceRegistry = new ServiceRegistry();
	private final List<BindingImpl<?>> elements = Lists.newArrayList();
	private final ImmutableSet<IModule> modules;

	public ModularBeanInjectorBuilder(Iterable<? extends IModule> modules) {
		this.modules = ImmutableSet.copyOf(modules);
	}

	public ServiceRegistry getRegistry() {
		return serviceRegistry;
	}
	
	public IBeanInjector toBeanInjector() {
		
		try {
			todo(serviceRegistry, modules);
		} catch (UnsatisfiedDependencyException | CyclicGraphException | ExecutionException
				| InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		return serviceRegistry.toBeanInjector(CallerContext.prototype());
	}

	private void todo(ServiceRegistry registry, ImmutableSet<IModule> modules) throws UnsatisfiedDependencyException, CyclicGraphException, ExecutionException, InterruptedException {
		
		IDependencyGraph<ExecutionUnit> dependencyGraph = buildGraph(modules, registry);
		
		// prints out the dependency tree to System.out
		new DependencyTreePrinter<>(dependencyGraph).toConsole();
		new ModuleRankingsPrinter<>(dependencyGraph).toConsole();

		walkGraph(dependencyGraph, modules, module -> module.activate(null));
	}
	
	private IDependencyGraph<ExecutionUnit> buildGraph(ImmutableSet<IModule> modules, ServiceRegistry registry) throws UnsatisfiedDependencyException, CyclicGraphException, InterruptedException, ExecutionException {
		Set<ExecutionUnit> units = modules.stream()
				.map(ExecutionUnit::new)
				.collect(GuavaCollectors.toImmutableSet());
		
		prepareBinders(registry, units);
		
		return DependencyGraphFactory.newInstance(units, m -> m.getDependencyDescriptor());
	}

	private List<ModuleBinder> prepareBinders(ServiceRegistry registry, Set<ExecutionUnit> units)
			throws InterruptedException, ExecutionException {
		
		List<ModuleBinder> binders = ExecutorUtils.parallelExecution(executor, units, u -> {
			ModuleBinder binder = new ModuleBinder(new TestBinder(), u); //Hack
			u.configure(binder);
			binder.build();
			return binder;
		});
		return binders;
	}
	
	public void walkGraph(IDependencyGraph<ExecutionUnit> dependencyGraph, ImmutableSet<IModule> modules, IConsumer<ExecutionUnit> consumer) throws ExecutionException {

		dependencyGraph.walkGraph(executor, module -> {
			String name = Thread.currentThread().getName();
			try {
				Thread.currentThread().setName(module.getName());
				consumer.process(module);
//				module.activate(beanInjector);
			} finally {
				Thread.currentThread().setName(name);
			}
		});
	}
}

package com.github.nill14.utils.java8.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;


public class ExecutorUtils {
	
	private ExecutorUtils() {	}

	/**
	 * Similar to parallel stream but executes in {@link ExecutorService} not {@link ForkJoinPool#commonPool()}.
	 * Another difference is that execution is always forked regardless of the job size.
	 * 
	 * 
	 * @param executor
	 * @param elements
	 * @param mappingFunction
	 * @return List of results from the mappingFunction
	 * @throws InterruptedException 
	 * @throws ExecutionException when mappingFunction throws exception
	 */
	public static <E, R> ImmutableList<R> parallelExecution(ExecutorService executor, Collection<E> elements,
			Function<E, R> mappingFunction) throws InterruptedException, ExecutionException {
		
		List<Future<R>> futures = new ArrayList<>(elements.size());
		for (E element : elements) {
			futures.add(executor.submit(() -> {
				return mappingFunction.apply(element);
			}));
		}
		
		ImmutableList.Builder<R> builder = ImmutableList.builder();
		for (Future<R> future : futures) {
			builder.add(future.get());
		}
		return builder.build();
	}
}

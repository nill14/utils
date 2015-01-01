package com.github.nill14.utils.java8.stream;

import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * <pre>
 * static import com.github.nill14.utils.java8.stream.GuavaCollectors.&#42;
 * </pre>
 */
public class GuavaCollectors {

	private GuavaCollectors() {
	}

	public static <T> Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> toImmutableSet() {
		
		Supplier<ImmutableSet.Builder<T>> supplier = ImmutableSet.Builder::new;
		BiConsumer<ImmutableSet.Builder<T>, T> accumulator = ImmutableSet.Builder<T>::add;
		BinaryOperator<ImmutableSet.Builder<T>> combiner = (left, right) -> {
			left.addAll(right.build());
			return left;
		};
		Function<ImmutableSet.Builder<T>, ImmutableSet<T>> finisher = ImmutableSet.Builder::build;
		EnumSet<Characteristics> characteristics = EnumSet.of(Characteristics.CONCURRENT,
				Characteristics.UNORDERED);

		return new CollectorImpl<T, ImmutableSet.Builder<T>, ImmutableSet<T>>(supplier, accumulator,
				combiner, finisher, characteristics);
	}
	
	public static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> toImmutableList() {
		return new CollectorImpl<T, ImmutableList.Builder<T>, ImmutableList<T>>(
				ImmutableList.Builder::new, 
				ImmutableList.Builder<T>::add,
				(left, right) -> {
					left.addAll(right.build());
					return left;
				}, 
				ImmutableList.Builder<T>::build, 
				EnumSet.of(Characteristics.CONCURRENT, Characteristics.UNORDERED));
	}	
}

package com.github.nill14.utils.java8.stream;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 
 * Utility class for streams
 *
 */
public class StreamUtils {

	private StreamUtils() {	}

	/**
	 * Convert iterator to stream.
	 *  
	 * @param iterator The iterator whose elements are converted to stream
	 * @return Stream
	 */
	public static <T> Stream<T> stream(Iterator<T> iterator) {
		Objects.requireNonNull(iterator);
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.IMMUTABLE),
				false);
	}

	/**
	 * Consider using {@link Stream#generate(Supplier)} followed by {@link Stream#limit(long)} 
	 * in case the element count is know in advance. 
	 * 
	 * @param hasNext The hasNext supplier
	 * @param next The value supplier
	 * @return A new stream
	 */
	public static <T> Stream<T> stream(Supplier<Boolean> hasNext, Supplier<T> next) {
		final Iterator<T> iterator = new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return hasNext.get();
			}

			@Override
			public T next() {
				return next.get();
			}
		};
		return stream(iterator);
	}

}

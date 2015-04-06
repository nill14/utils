package com.github.nill14.utils.java8.stream;

import static org.testng.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.Test;

public class StreamUtilsTest {

	@Test
	public void testStreamIteratorOfT() {
		Stream<Integer> stream = StreamUtils.stream(new Iterator<Integer>() {
			private int counter = 0;

			@Override
			public boolean hasNext() {
				return counter < 10;
			}

			@Override
			public Integer next() {
				return ++counter;
			}
		});
		
		List<Integer> list = stream.collect(Collectors.toList());
		assertEquals(10, list.size());
		assertTrue(list.contains(5));
		assertFalse(list.contains(15));
	}

	@Test
	public void testStreamSupplierOfBooleanSupplierOfT() {
		Stream<String> stream = StreamUtils.stream(() -> true, () -> "echo");
		Optional<String> optional = stream.skip(20).limit(1).filter(e -> "echo".equals(e)).findFirst();
		assertTrue(optional.isPresent());
	}

}

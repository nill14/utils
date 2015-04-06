package com.github.nill14.utils.java8.stream;

import java.util.stream.IntStream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.*;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class GuavaCollectorsTest {

	
	private IntStream range() {
		return IntStream.rangeClosed(1, 10);
	}
	
	private IntStream duplicateRange() {
		return IntStream.concat(range(), range());
	}
	
	private Stream<String> echoN() {
		return duplicateRange().mapToObj(i -> "echo-"+i);
	}
	
	@Test
	public void testToImmutableSet() {
		ImmutableSet<String> set = echoN().collect(GuavaCollectors.toImmutableSet());
		assertEquals(10, set.size());
		assertTrue(set.contains("echo-5"));
		assertFalse(set.contains("echo-15"));
	}

	@Test
	public void testToImmutableList() {
		ImmutableList<String> list = echoN().collect(GuavaCollectors.toImmutableList());
		assertEquals(20, list.size());
		assertTrue(list.contains("echo-5"));
		assertFalse(list.contains("echo-15"));
	}

}

package com.github.nill14.utils.init.impl;

import static org.testng.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class ParameterizedTypeTest {

	private final List<String> list = new ArrayList<>();
	
	@Test
	public void test() throws NoSuchFieldException, SecurityException {
		Field field = ParameterizedTypeTest.class.getDeclaredField("list");
		ParameterizedType genericType = (ParameterizedType) field.getGenericType();
		Type paramType = genericType.getActualTypeArguments()[0];
		Class<?> cls = (Class<?>) paramType;
		assertEquals(List.class, field.getType());
		assertEquals(String.class, cls);
	}

}

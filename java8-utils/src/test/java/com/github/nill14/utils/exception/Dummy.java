package com.github.nill14.utils.exception;

public class Dummy {
	public static String call(int level) {
		return another(level);
	}
	
	public static String another(int level) {
		return StackTraceParser.getCalledMethod(new RuntimeException(), level);
	}
}

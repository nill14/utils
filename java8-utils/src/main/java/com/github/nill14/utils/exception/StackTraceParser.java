package com.github.nill14.utils.exception;

import java.util.NoSuchElementException;

public enum StackTraceParser {
	;

	/**
	 * 
	 * @param level The caller className level, must be bigger than zero
	 * @return e.g. "(Dummy.java:9): getCalledMethod()" for level=1
	 */
	public static String getCalledMethod(Exception exception, int level) {
		StackTraceElement[] stackTrace = exception.getStackTrace();
		
		int index = findNthFilename(stackTrace, level);
		
		String methodName = stackTrace[index-1].getMethodName();
		String callerReference = formatReference(stackTrace[index]);
		
		return String.format("%s: %s() ", callerReference, methodName);
	}

	public static String formatReference(StackTraceElement element) {
		String filename = element.getFileName();
		int lineNumber = element.getLineNumber();

		if (filename != null && lineNumber >= 0) {
			return String.format("(%s:%d)", filename, lineNumber);

		} else if (filename != null) {
			return String.format("(%s)", filename);

		} else {
			return "(Unknown Source)";
		}
	}

	public static int findNthFilename(StackTraceElement[] stackTrace, int level) {
		if (level <= 0) {
			throw new IllegalArgumentException(String.format("Level = %s, expected >= 1", level));
		}
		if (level >= stackTrace.length) {
			throw new IllegalArgumentException("Level is far too big");
		}
		
		int index = -1;
		String classname = null;
		for (int i =0; i < stackTrace.length; i++) {
			StackTraceElement element = stackTrace[i];
			if (classname == null || !classname.equals(element.getClassName())) {
				classname = element.getClassName();
				if (++index == level) {
					return i;
				}
			}
		}
		throw new NoSuchElementException(String.format("The last level was %d, requested %d", index, level));
	}

}

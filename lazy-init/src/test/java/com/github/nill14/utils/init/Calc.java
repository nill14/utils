package com.github.nill14.utils.init;

import java.io.Serializable;

public class Calc implements ICalc, Serializable {

	private static final long serialVersionUID = -5998894544071451169L;

	@Override
	public int add(int a, int b) {
		return a + b;
	}
	
	@Override
	public String toString() {
		String hexString = Integer.toHexString(System.identityHashCode(this));
		return String.format("Calc@%s", hexString);
	}

}

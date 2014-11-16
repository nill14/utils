package com.github.nill14.utils.init;

public class Calc implements ICalc {

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

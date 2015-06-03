package com.github.nill14.utils.init;

public class Greeter implements IGreeter {
	
	private final String greeting;

	public Greeter(String greeting) {
		this.greeting = greeting;
	}

	@Override
	public String sayGreeting() {
		return greeting;
	}

}

package com.github.nill14.utils.init;

import javax.inject.Inject;
import javax.inject.Provider;

public class GreeterFactory implements Provider<IGreeter> {

	@Inject
	private String greeting;
	
	@Override
	public IGreeter get() {
		return new Greeter(greeting);
	}

	public String getGreeting() {
		return greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}
	
	

}

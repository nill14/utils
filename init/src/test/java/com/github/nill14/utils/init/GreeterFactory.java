package com.github.nill14.utils.init;

import javax.inject.Inject;

import com.github.nill14.utils.init.api.IPojoFactory;

public class GreeterFactory implements IPojoFactory<IGreeter> {

	@Inject
	private String greeting;
	
	@Override
	public IGreeter newInstance() {
		return new Greeter(greeting);
	}

	@Override
	public Class<IGreeter> getType() {
		return IGreeter.class;
	}

	public String getGreeting() {
		return greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}
	
	

}

package com.github.nill14.utils.init;

import javax.inject.Provider;

import org.mockito.Mockito;

public class TimeServiceSpyFactory implements Provider<TimeService> {

	@Override
	public TimeService get() {
		return Mockito.mock(TimeService.class);
	}


}

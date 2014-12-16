package com.github.nill14.utils.init;

import org.mockito.Mockito;

import com.github.nill14.utils.init.api.IPojoFactory;

public class TimeServiceSpyFactory implements IPojoFactory<TimeService> {

	@Override
	public TimeService newInstance() {
		return Mockito.mock(TimeService.class);
	}

	@Override
	public Class<TimeService> getType() {
		return TimeService.class;
	}

}

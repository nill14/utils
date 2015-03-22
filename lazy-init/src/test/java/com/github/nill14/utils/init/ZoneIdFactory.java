package com.github.nill14.utils.init;

import java.time.ZoneId;

import javax.inject.Provider;

public class ZoneIdFactory implements Provider<ZoneId> {	

	@Override
	public ZoneId get() {
		return ZoneId.systemDefault();
	}

}

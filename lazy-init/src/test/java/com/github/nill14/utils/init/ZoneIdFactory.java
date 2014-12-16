package com.github.nill14.utils.init;

import java.time.ZoneId;

import com.github.nill14.utils.init.api.IPojoFactory;

public class ZoneIdFactory implements IPojoFactory<ZoneId> {

	private static final long serialVersionUID = -6908601084650269667L;

	@Override
	public ZoneId newInstance() {
		return ZoneId.systemDefault();
	}

	@Override
	public Class<ZoneId> getType() {
		return ZoneId.class;
	}

}

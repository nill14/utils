package com.github.nill14.utils.init.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;

public final class EmptyPojoInitializer implements IPojoInitializer {

	private static final long serialVersionUID = 2837451571146010916L;
	
	private static final EmptyPojoInitializer instance = new EmptyPojoInitializer();
	public static final  IPojoInitializer getInstance() {
		return instance;
	}
	
	private EmptyPojoInitializer() {
	}
	
	@Override
	public void init(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) { }

	@Override
	public void destroy(ILazyPojo<?> lazyPojo, IPojoFactory<?> pojoFactory, Object instance) { }

	private void writeObject(ObjectOutputStream stream) throws IOException { }

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException { }

	public Object readResolve() {
		return getInstance();
	}
	
}

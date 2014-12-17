package com.github.nill14.utils.init.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.github.nill14.utils.init.api.IPojoInitializer;

public final class EmptyPojoInitializer<T> implements IPojoInitializer<T> {

	private static final long serialVersionUID = 2837451571146010916L;
	
	private static final EmptyPojoInitializer<?> instance = new EmptyPojoInitializer<>();
	@SuppressWarnings("unchecked")
	public static final <T> IPojoInitializer<T> getInstance() {
		return (IPojoInitializer<T>) instance;
	}
	
	private EmptyPojoInitializer() {
	}
	
	@Override
	public void init(T instance) { }

	@Override
	public void destroy(T instance) { }

	private void writeObject(ObjectOutputStream stream) throws IOException { }

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException { }

	public Object readResolve() {
		return getInstance();
	}
	
}

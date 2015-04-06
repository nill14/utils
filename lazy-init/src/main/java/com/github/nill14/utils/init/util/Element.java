package com.github.nill14.utils.init.util;

import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Preconditions;

public class Element<E> {
	
	private final AtomicBoolean locker;
	private E value;

	public Element(AtomicBoolean locker) {
		this.locker = locker;
	}


	public void update(E element) {
		Preconditions.checkNotNull(element);
		if (!locker.get()) {
			synchronized (this) {
				value = element;
			}
		} else {
			throw new RuntimeException("Cannot update the value after the module was configured!");
		}
	}
	
	public E getValue() {
		synchronized (this) {
			return value;
		}
	}
	
	
	
}

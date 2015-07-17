package com.github.nill14.utils.init.impl;

import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import com.github.nill14.utils.init.api.BindingKey;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;
import com.google.common.reflect.TypeToken;

final class ConstructionContext {
	
	private final CallerContext context;
	private final BindingKey<?> bindingKey;
	private Object instance;
	private boolean constructing = true;
	private CircularProxyInvocationHandler invocationHandler;

	public ConstructionContext(CallerContext context, BindingKey<?> bindingKey) {
		this.context = context;
		this.bindingKey = bindingKey;
	
	}

	public <T> T getInstance(Class<T> realType) throws NoSuchElementException, ClassCastException {
		if (instance != null) {
			return realType.cast(instance);
			
		} else if (realType.isInterface()) {
			return Reflection.newProxy(realType, getInvocationHandler());
			
		} else {
			throw new RuntimeException(String.format(
				"Detected circular dependency involving %s. Decompose your code or use interfaces for creation of dynamic proxies.", 
				bindingKey.getRawType()));

		}
	}

	public void finishConstructing() {
		if (!constructing) {
			throw new IllegalStateException("Can construct only once");
		}
		constructing = false;
		context.stopConstructing(bindingKey);
	}

	public void setInstance(Object instance) {
		if (this.instance != null) {
			throw new RuntimeException("Double initialization!");
		}

		setInstance0(instance);
	}
	
	public void setInstanceIfUnset(Object instance) {
		if (!constructing) {
			throw new IllegalStateException("Cannot set instance without constructing!");
		
		} else if (this.instance != null) {
			return;
		}
		
		setInstance0(instance);
	}
	
	private void setInstance0(Object instance) {
		if (instance != null && !bindingKey.getRawType().isAssignableFrom(instance.getClass())) {
			throw new ClassCastException(String.format("%s cannot be cast to %s", instance.getClass(), bindingKey.getRawType()));
		}
		
		this.instance = instance;
		
		if (invocationHandler != null && instance != null) {
			invocationHandler.setInstance(instance);
		}
	}

	public TypeToken<?> getToken() {
		return bindingKey.getToken();
	}
	
	public CircularProxyInvocationHandler getInvocationHandler() {
		if (invocationHandler == null) {
			invocationHandler = new CircularProxyInvocationHandler();
		}
		return invocationHandler;
	}

	@Override
	public String toString() {
		return String.format("ConstructionContext [%s]", bindingKey);
	}
	
	

}

class CircularProxyInvocationHandler extends AbstractInvocationHandler {
	
	private Object instance = null;
	
	@Override
	protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
		if (instance == null) {
			throw new RuntimeException("Circular proxy - The object is not yet constructed. - Call it later.");
		}
		return method.invoke(instance, args);
	}
	
	public void setInstance(Object instance) {
		this.instance = instance;
	}
}

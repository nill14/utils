package com.github.nill14.utils.init.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Set;

import com.github.nill14.utils.init.api.ILazyObject;
import com.github.nill14.utils.init.api.IObjectFactory;
import com.github.nill14.utils.init.api.IObjectInitializer;
import com.google.common.collect.Sets;

public class LazyJdkProxy implements InvocationHandler {

	public static <S, T extends S> S newProxy(Class<S> iface, Class<T> beanClass) {
		return iface.cast(newProxy(beanClass));
	}

	public static <S, T extends S> S newProxy(Class<S> iface, ILazyObject<T> delegate) {
		return iface.cast(newProxy(delegate));
	}
	
	public static Object newProxy(Class<?> beanClass) {
		IObjectFactory<?> factory = BeanObjectFactory.create(beanClass);
		ILazyObject<?> delegate = new LazyObject<>(factory, IObjectInitializer.empty());
		return newProxy(delegate);
	}
	
	public static Object newProxy(ILazyObject<?> delegate) {
		LazyJdkProxy invocationHandler = new LazyJdkProxy(delegate);
		Class<?> clazz = delegate.getInstanceType();
		ClassLoader cl = clazz.getClassLoader();
		Class<?>[] ifaces = getImplementedInterfaces(clazz);
		return Proxy.newProxyInstance(cl, ifaces, invocationHandler);
	}

    private  static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaces = Sets.newHashSet();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            interfaces.addAll(Arrays.<Class<?>>asList(c.getInterfaces()));
        }
        return interfaces.stream().toArray(Class[]::new);
    }
	
	private final ILazyObject<?> delegate;

	private LazyJdkProxy(ILazyObject<?> delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (objectEqualsMethod.equals(method)) {
			return proxy == args[0];
			
		} else if (objectHashCodeMethod.equals(method)) {
			return System.identityHashCode(proxy);
			
		} else if (objectToStringMethod.equals(method)) {
			String hex = Integer.toHexString(System.identityHashCode(proxy));
			String className = delegate.getInstanceType().getName();
			return String.format("%s@%s (Proxy)", className, hex);
		}
		
		try {
			return method.invoke(delegate.getInstance(), args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
	
	private static Method getMethod(Class<?> type, String name, Class<?>... argTypes) {
        try {
            return type.getMethod(name, argTypes);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
	
	private static final Method objectEqualsMethod = getMethod(Object.class, "equals", Object.class);
	private static final Method objectHashCodeMethod = getMethod(Object.class, "hashCode");
	private static final Method objectToStringMethod = getMethod(Object.class, "toString");
}

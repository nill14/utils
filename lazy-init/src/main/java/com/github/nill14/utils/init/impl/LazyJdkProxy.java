package com.github.nill14.utils.init.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.google.common.reflect.TypeToken;

public class LazyJdkProxy implements InvocationHandler, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3530731678963079055L;

	public static <S, T extends S> S newProxy(Class<S> iface, Class<T> beanClass) {
		return iface.cast(newProxy(beanClass));
	}

	public static <S, T extends S> S newProxy(Class<S> iface, ILazyPojo<T> lazyPojo) {
		return iface.cast(newProxy(lazyPojo));
	}
	
	public static <T> Object newProxy(Class<T> beanClass) {
		ILazyPojo<?> lazyPojo = LazyPojo.forBean(beanClass);
		return newProxy(lazyPojo);
	}
	
	public static <T> Object newProxy(ILazyPojo<T> lazyPojo) {
		LazyJdkProxy invocationHandler = new LazyJdkProxy(lazyPojo);
		TypeToken<T> token = lazyPojo.getType();
		ClassLoader cl = token.getRawType().getClassLoader();
		Class<?>[] ifaces = token.getTypes().interfaces().rawTypes().stream().toArray(Class[]::new);
		return Proxy.newProxyInstance(cl, ifaces, invocationHandler);
	}

	/**
	 * Normally, the implemented interfaces are detected automatically. Prefer {@link #newProxy(ILazyPojo)} 
	 * 
	 * @param lazyPojo
	 * @param classLoader
	 * @param implementedInterfaces
	 * @return Proxy
	 */
	public static <T> Object newProxy(ILazyPojo<T> lazyPojo, ClassLoader classLoader, Class<? super T>[] implementedInterfaces) {
		LazyJdkProxy invocationHandler = new LazyJdkProxy(lazyPojo);
		return Proxy.newProxyInstance(classLoader, implementedInterfaces, invocationHandler);
	}
	
	private final ILazyPojo<?> delegate;

	private LazyJdkProxy(ILazyPojo<?> delegate) {
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
			String className = delegate.getType().getRawType().getName();
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
            return type.getDeclaredMethod(name, argTypes);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
	
	private static final Method objectEqualsMethod = getMethod(Object.class, "equals", Object.class);
	private static final Method objectHashCodeMethod = getMethod(Object.class, "hashCode");
	private static final Method objectToStringMethod = getMethod(Object.class, "toString");
}

package com.github.nill14.utils.init.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.google.common.collect.Sets;

public class LazyJavassistProxy implements MethodHandler, MethodFilter, Serializable {

	private static final long serialVersionUID = 3530731678963079055L;

	public static <S, T extends S> S newProxy(Class<S> iface, Class<T> beanClass) {
		return iface.cast(newProxy(beanClass));
	}

	public static <S, T extends S> S newProxy(Class<S> iface, ILazyPojo<T> lazyPojo) {
		return iface.cast(newProxy(lazyPojo));
	}
	
	public static Object newProxy(Class<?> beanClass) {
		ILazyPojo<?> lazyPojo = LazyPojo.forClass(beanClass);
		return newProxy(lazyPojo);
	}
	
	public static Object newProxy(ILazyPojo<?> lazyPojo) {
		LazyJavassistProxy methodHandler = new LazyJavassistProxy(lazyPojo);
		Class<?> clazz = lazyPojo.getInstanceType();
		Class<?>[] ifaces = getImplementedInterfaces(clazz);

		ProxyFactory f = new ProxyFactory();
		if (!clazz.isInterface()) {
			f.setSuperclass(clazz);
		}
		f.setInterfaces(ifaces);
		f.setFilter(methodHandler);
		Class<?> c = f.createClass();
		try {
			Proxy foo = (Proxy) c.newInstance();
			foo.setHandler(methodHandler);
			return foo;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Cannot create a proxy", e);
		} 
	}

    private  static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaces = Sets.newHashSet();
        if (clazz.isInterface()) {
        	interfaces.add(clazz);
        }
        
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            interfaces.addAll(Arrays.<Class<?>>asList(c.getInterfaces()));
        }
        return interfaces.stream().toArray(Class[]::new);
    }
	
	private final ILazyPojo<?> delegate;

	private LazyJavassistProxy(ILazyPojo<?> delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public boolean isHandled(Method m) {
		if(signatureEquals(objectFinalizeMethod, m)) {
			return false;
		} else if (signatureEquals(objectEqualsMethod, m)) {
			return false;
		} else if (signatureEquals(objectHashCodeMethod, m)) {
			return false;
		} else if (signatureEquals(objectToStringMethod, m)) {
			return false;
		}
		return true;
	}

	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed,
			Object[] args) throws Throwable {
		
		try {
			return thisMethod.invoke(delegate.getInstance(), args);
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

	private static boolean signatureEquals(Method method, Method otherMethod) {
	    return method.getDeclaringClass().isAssignableFrom(otherMethod.getDeclaringClass()) &&
	    		method.getName().equals(otherMethod.getName()) && 
	    		Arrays.equals(method.getParameterTypes(), otherMethod.getParameterTypes());
    }
	
	private static final Method objectEqualsMethod = getMethod(Object.class, "equals", Object.class);
	private static final Method objectHashCodeMethod = getMethod(Object.class, "hashCode");
	private static final Method objectToStringMethod = getMethod(Object.class, "toString");
	private static final Method objectFinalizeMethod = getMethod(Object.class, "finalize");

}

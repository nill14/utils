package com.github.nill14.utils.init.inject;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.github.nill14.utils.init.api.IBeanDescriptor;
import com.github.nill14.utils.init.api.IParameterType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;


@SuppressWarnings("serial")
public class PojoInjectionDescriptor<T> implements Serializable, IBeanDescriptor<T> {

	
	private final ImmutableList<FieldInjectionDescriptor> fields;
	private final ImmutableList<MethodInjectionDescriptor> methods;
	private final ImmutableList<ConstructorInjectionDescriptor> constructors;

	private final TypeToken<T> typeToken;
	private final Set<Class<? super T>> interfaces;

	@SuppressWarnings("unchecked")
	public PojoInjectionDescriptor(IParameterType parameterType) {
		this((TypeToken<T>) TypeToken.of(parameterType.getGenericType()));
	}
	
	@SuppressWarnings("unchecked")
	public PojoInjectionDescriptor(Class<? extends T> pojoClazz) {
		this(TypeToken.of((Class<T>) pojoClazz));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PojoInjectionDescriptor(TypeToken<T> typeToken) {
		Preconditions.checkNotNull(typeToken);
		this.typeToken = typeToken;
		Set<Class<?>> classes = (Set) typeToken.getTypes().classes().rawTypes();
		interfaces = typeToken.getTypes().interfaces().rawTypes();
		
		fields = ImmutableList.copyOf(
				injectableFields(nonStaticFields(classes.stream()), typeToken.getRawType()).iterator());
		
		methods = ImmutableList.copyOf(
				injectableMethods(nonStaticMethods(classes.stream()), typeToken.getRawType()).iterator());
		
		constructors = ImmutableList.copyOf(
				injectableConstructors(Stream.of(typeToken.getRawType().getDeclaredConstructors())).iterator());
		
//		ImmutableList.Builder<IParameterType> builder = ImmutableList.builder();
//		for (FieldInjectionDescriptor f : fields) {
//			builder.add(f.getParameterType());
//		}
//		for (MethodInjectionDescriptor m : methods) {
//			builder.addAll(m.getParameterTypes());
//		}
//		TODO gather optional and mandatory dependencies 
	}
	
	@Override
	public ImmutableList<FieldInjectionDescriptor> getFieldDescriptors() {
		return fields;
	}

	@Override
	public ImmutableList<MethodInjectionDescriptor> getMethodDescriptors() {
		return methods;
	}
	
	@Override
	public ImmutableList<ConstructorInjectionDescriptor> getConstructorDescriptors() {
		return constructors;
	}

	
	@Override
	public Set<Class<? super T>> getInterfaces() {
		return interfaces;
	}
	
	@Override
	public Set<Class<? super T>> getDeclaredTypes() {
		return typeToken.getTypes().rawTypes();
	}
	
	private Stream<Method> nonStaticMethods(Stream<Class<?>> declaredClasses) {
		return declaredClasses
			.flatMap(c -> Stream.of(c.getDeclaredMethods()))
			.filter(f -> !Modifier.isStatic(f.getModifiers()));
	}

	private Stream<Field> nonStaticFields(Stream<Class<?>> declaredClasses) {
		return declaredClasses
				.flatMap(c -> Stream.of(c.getDeclaredFields()))
				.filter(f -> !Modifier.isStatic(f.getModifiers()));
	}

	private Stream<? extends ConstructorInjectionDescriptor> injectableConstructors(Stream<Constructor<?>> constructors) {
		return constructors.map(c -> {
			if (c.getParameterCount() == 0 
					|| c.isAnnotationPresent(javax.inject.Inject.class) 
					|| c.isAnnotationPresent(com.google.inject.Inject.class)) {
				return new ConstructorInjectionDescriptor(c);
			} 
			else return null;
		}).filter(x -> x != null);
	}	
	
	
	private Stream<? extends MethodInjectionDescriptor> injectableMethods(Stream<Method> nonStaticMethods, Class<?> declaringClass) {
		return nonStaticMethods.map(m -> {
			if (m.isAnnotationPresent(javax.inject.Inject.class) 
					|| m.isAnnotationPresent(com.google.inject.Inject.class)) {
				return new MethodInjectionDescriptor(m, declaringClass);
			} 
			else return null;
		}).filter(x -> x != null);
	}	

	private Stream<? extends FieldInjectionDescriptor> injectableFields(Stream<Field> nonStaticFields, Class<?> declaringClass) {
		return nonStaticFields.map(f -> {
			if (f.isAnnotationPresent(javax.inject.Inject.class) 
					|| f.isAnnotationPresent(com.google.inject.Inject.class)) {
				return new FieldInjectionDescriptor(f, declaringClass);
			} 
			else return null;
		}).filter(x -> x != null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getRawType() {
		return (Class<T>) typeToken.getRawType();
	}
	
	@Override
	public Type getGenericType() {
		return typeToken.getType();
	}

	@Override
	public boolean canBeInstantiated() {
		Class<?> clazz = typeToken.getRawType();
		return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()) && !constructors.isEmpty();
	}	

	@Override
	public String toString() {
		return typeToken.toString();
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		throw new InvalidObjectException("Proxy required");
	}
	
    private Object writeReplace() {
        return new SerializableProxy(this);
    }
    
    private static class SerializableProxy implements Serializable {
    	private final TypeToken<?>  token;

    	public SerializableProxy(PojoInjectionDescriptor<?> pd) {
			this.token = pd.typeToken;
		}
    	
    	private Object readResolve() {
    		return new PojoInjectionDescriptor<>(token);
    	}
    }

	@Override
	public TypeToken<T> getToken() {
		return typeToken;
	}
}
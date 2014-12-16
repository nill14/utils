package com.github.nill14.utils.init;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.nill14.utils.init.api.ILazyPojo;
import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.AnnotationPojoInitializer;
import com.github.nill14.utils.init.impl.EmptyPojoInitializer;
import com.github.nill14.utils.init.impl.LazyJdkProxy;
import com.github.nill14.utils.init.impl.LazyPojo;
import com.github.nill14.utils.init.impl.PojoFactory;
import com.github.nill14.utils.init.impl.ServiceRegistry;

public class SerializationTest {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	@Before
	public void setUp() throws IOException {
		PipedInputStream in = new PipedInputStream(100000);
		PipedOutputStream out = new PipedOutputStream(in);
		
		oos = new ObjectOutputStream(out);
		ois = new ObjectInputStream(in);
		
	}
	
	@After
	public void cleanUp() throws IOException {
		ois.close();
		oos.close();
	}
	
	public <T> Object doTest(T instance, Class<? extends T> clazz) throws IOException, ClassNotFoundException {
		oos.writeObject(instance);
		Object object = ois.readObject();
		assertThat(object, instanceOf(clazz));
		return object;
	}
	
	public <T> Object doTestEquals(T instance, Class<? extends T> clazz) throws IOException, ClassNotFoundException {
		Object object = doTest(instance, clazz);
		assertEquals(instance, object);
		return object;
	}
	
	@Test(expected=NotSerializableException.class, timeout=5000)
	public void testUnserializable() throws IOException, ClassNotFoundException {
		doTestEquals(this, Object.class);
	}
	
	@Test(timeout=5000)
	public void testInteger() throws IOException, ClassNotFoundException {
		Integer integer = new Integer(42);
		doTestEquals(integer, Integer.class);
	}
	
	@Test(timeout=5000)
	public void testEmptyPojoInitializer() throws IOException, ClassNotFoundException {
		IPojoInitializer<Object> initializer = EmptyPojoInitializer.getInstance();
		doTestEquals(initializer, EmptyPojoInitializer.class);
	}

	@Test(timeout=5000)
	public void testLazyProxy() throws IOException, ClassNotFoundException {
		ICalc lazyProxy = LazyJdkProxy.newProxy(ICalc.class, Calc.class);
		lazyProxy.add(5, 3); //lazy Calc initialization
		doTest(lazyProxy, ICalc.class);
	}

	@Test(timeout=5000)
	public void testComplex() throws IOException, ClassNotFoundException {
		IPropertyResolver resolver = Mockito.mock(IPropertyResolver.class);
		IPojoInitializer<Object> initializer = AnnotationPojoInitializer.withResolver(resolver);
		IPojoFactory<Calc> pojoFactory = PojoFactory.create(Calc.class);
		ILazyPojo<ICalc> calcPojo = new LazyPojo<>(pojoFactory, initializer);
		ICalc calc = LazyJdkProxy.newProxy(ICalc.class, calcPojo);
		calc.add(5, 3); //lazy Calc initialization
		
		doTest(calc, ICalc.class);
	}
	
	
	@Test(expected=NotSerializableException.class, timeout=5000)
	public void testServiceRegistry() throws IOException, ClassNotFoundException {
		IServiceRegistry registry = new ServiceRegistry();
		doTestEquals(registry, IServiceRegistry.class);
	}
}

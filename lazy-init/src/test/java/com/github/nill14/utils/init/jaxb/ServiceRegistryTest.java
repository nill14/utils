package com.github.nill14.utils.init.jaxb;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.nill14.utils.init.ICalculator;
import com.github.nill14.utils.init.IGreeter;
import com.github.nill14.utils.init.ITimeService;
import com.github.nill14.utils.init.api.IServiceRegistry;

public class ServiceRegistryTest {

	
	private static IServiceRegistry registry;


	@BeforeClass
	public static void setUp() throws IOException {

        try {
        	try (InputStream inputStream = 
            		JaxbDemo.class.getClassLoader().getResourceAsStream("registry.xml")) {
        		
        		registry = new JaxbLoader().load(inputStream);
        	}
        } catch (JAXBException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        	throw new RuntimeException(e);
		} 
	}
	
	
	@Test
	public void testCalc() {
		ICalculator calc = registry.getService(ICalculator.class);
		assertEquals(8, calc.add(5, 3));
	}

	@Test
	public void testGreeter() {
		IGreeter service = registry.getService(IGreeter.class);
		assertEquals("Hello World!", service.sayGreeting());
	}
	
	@Test
	public void testTimeService() {
		ITimeService service = registry.getService(ITimeService.class);
		assertNotNull(service.getNow());
		assertEquals(1, service.getProviders().size());
	}
}

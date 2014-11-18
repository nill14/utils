package com.github.nill14.utils.init.jaxb;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.github.nill14.utils.init.schema.ObjectFactory;
import com.github.nill14.utils.init.schema.Service;
import com.github.nill14.utils.init.schema.ServiceRegistry;
 
public class JaxbDemo {
 
    public static void main(String[] args) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance("com.github.nill14.utils.init.schema");
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        InputStream inputStream = 
        		JaxbDemo.class.getClassLoader().getResourceAsStream("registry.xml");
//        Object unmarshal = jaxbUnmarshaller.unmarshal(inputStream);
        JAXBElement<ServiceRegistry> element = jaxbUnmarshaller.unmarshal(new StreamSource(inputStream), ServiceRegistry.class);
//		ObjectFactory registry = (ObjectFactory) unmarshal;
        ServiceRegistry registry = element.getValue();
		System.out.println(registry);
		
//		ObjectFactory objFactory = new ObjectFactory();
//		registry.setResolver("resolver");
//		registry.setServices(objFactory.createServices());
//		Service service = objFactory.createService();
//		service.setBean("bean");
//		service.setInterface("iface");
//		registry.getServices().getService().add(service);
		
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
 
		jaxbMarshaller.marshal(element, System.out);
    }
 
}
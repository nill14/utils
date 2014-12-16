package com.github.nill14.utils.init.jaxb;
import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.github.nill14.utils.init.api.IPojoFactory;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.impl.PojoFactory;
import com.github.nill14.utils.init.schema.BeanProperty;
import com.github.nill14.utils.init.schema.FactoryProperty;
import com.github.nill14.utils.init.schema.Provider;
import com.github.nill14.utils.init.schema.Service;
import com.github.nill14.utils.init.schema.ServiceRegistry;
import com.github.nill14.utils.init.schema.StringProperty;
import com.google.common.collect.Maps;
 
public class JaxbLoader {
 

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IServiceRegistry load(InputStream inputStream) throws JAXBException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        JAXBContext jaxbContext = JAXBContext.newInstance("com.github.nill14.utils.init.schema");
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        JAXBElement<ServiceRegistry> element = jaxbUnmarshaller.unmarshal(new StreamSource(inputStream), ServiceRegistry.class);
        ServiceRegistry registry = element.getValue();
		
        com.github.nill14.utils.init.impl.ServiceRegistry serviceRegistry = new com.github.nill14.utils.init.impl.ServiceRegistry();
        
        if (registry.getServices() != null) {
        	for (Service service : registry.getServices().getService()) {
        		Class iface = Class.forName(service.getInterface());
        		if (service.getBean() != null) {
        			Class serviceBean = Class.forName(service.getBean());
        			serviceRegistry.putService(iface, serviceBean);
        		} else {
        			Class factoryBean = Class.forName(service.getFactory());
        			serviceRegistry.putServiceFactory(iface, factoryBean);
        		}
        	}
        }

        if (registry.getProviders() != null) {
        	for (Provider service : registry.getProviders().getProvider()) {
        		Class registrable = Class.forName(service.getRegistrable());
        		for (String bean : service.getProvider()) {
        			Class providerClass = Class.forName(bean);
        			serviceRegistry.addProvider(registrable, providerClass);
        		}
        		for (String bean : service.getProviderFactory()) {
        			Class providerFactoryClass = Class.forName(bean);
        			serviceRegistry.addProviderFactory(registrable, providerFactoryClass);
        		}
        	}
        }
        
        Map<String, String> strings = Maps.newHashMap();
        Map<Class, IPojoFactory> factories = Maps.newHashMap();
        if (registry.getProperties() != null) {
        	for (StringProperty property : registry.getProperties().getString()) {
    			strings.put(property.getName(), property.getValue());
        	}
        	for (BeanProperty property : registry.getProperties().getBean()) {
        		Class beanClass = Class.forName(property.getValue());
    			factories.put(beanClass, PojoFactory.create(beanClass));
        	}
        	for (FactoryProperty property : registry.getProperties().getFactory()) {
        		IPojoFactory factory = (IPojoFactory) Class.forName(property.getValue()).newInstance();
        		factories.put(factory.getType(), factory);
        	}
        	
        	serviceRegistry.setDelegateResolver(new IPropertyResolver() {
        		
				private static final long serialVersionUID = 6911651120730545150L;

				@Override
        		public Object resolve(Object pojo, Class<?> propertyType, String propertyName) {
        			if (propertyType == String.class) {
        				String value = strings.get(propertyName);
        				if (value != null) {
        					return value;
        				}
        			} 
        			IPojoFactory factory = factories.get(propertyType);
        			if (factory != null) {
        				return factory.newInstance();
        			}
					return null;
        		}
        	});
        }

        return serviceRegistry;
    }
 
}
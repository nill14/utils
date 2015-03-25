package com.github.nill14.utils.init.jaxb;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.transform.stream.StreamSource;

import com.github.nill14.utils.init.api.IParameterType;
import com.github.nill14.utils.init.api.IPojoInitializer;
import com.github.nill14.utils.init.api.IPropertyResolver;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.init.schema.BeanProperty;
import com.github.nill14.utils.init.schema.FactoryProperty;
import com.github.nill14.utils.init.schema.Service;
import com.github.nill14.utils.init.schema.ServiceRegistry;
import com.github.nill14.utils.init.schema.StringProperty;
import com.google.common.collect.Maps;
 
public class JaxbLoader {
 

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IServiceRegistry load(InputStream inputStream) throws JAXBException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ServiceRegistry.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        
        
        JAXBElement<ServiceRegistry> element = unmarshaller.unmarshal(new StreamSource(inputStream), ServiceRegistry.class);
        ServiceRegistry registry = element.getValue();
		
        Map<String, String> strings = Maps.newHashMap();
        if (registry.getProperties() != null) {
        	for (StringProperty property : registry.getProperties().getStrings()) {
        		strings.put(property.getName(), property.getValue());
        	}
        }

        IServiceRegistry serviceRegistry = IServiceRegistry.newRegistry();
        IServiceContext jaxbServiceContext = new IServiceContext() {
        	
        	@Override
        	public Optional<IPojoInitializer> getInitializer() {
        		return Optional.empty();
        	}
        	
        	@Override
        	public Optional<IPropertyResolver> getCustomResolver() {
        		return Optional.of(new IPropertyResolver() {
        			
        			private static final long serialVersionUID = 6911651120730545150L;
        			
        			@Override
        			public Object resolve(Object pojo, IParameterType type) {
        				if (type.getRawType() == String.class) {
        					String value = strings.get(type.getNamed().orElse(null));
        					if (value != null) {
        						return value;
        					}
        				} 
        				return serviceRegistry.toResolver();
        			}
        		});
        	}
        };
        
        if (registry.getServices() != null) {
        	for (Service service : registry.getServices()) {
        		Class iface = Class.forName(service.getInterface());
        		if (service.getBean() != null) {
        			Class serviceBean = Class.forName(service.getBean());
        			serviceRegistry.addService(serviceBean, jaxbServiceContext);
        		} else {
        			Class factoryBean = Class.forName(service.getFactory());
        			serviceRegistry.addServiceFactory(iface, factoryBean, jaxbServiceContext);
        		}
        	}
        }
        if (registry.getProperties() != null) {
        	for (BeanProperty property : registry.getProperties().getBeen()) {
        		Class beanClass = Class.forName(property.getValue());
        		serviceRegistry.addService(beanClass, jaxbServiceContext);
        	}
        	for (FactoryProperty property : registry.getProperties().getFactories()) {
        		Class factoryBean = Class.forName(property.getValue());
        		Class iface = factoryBean.getMethod("get").getReturnType();
        		serviceRegistry.addServiceFactory(iface, factoryBean, jaxbServiceContext);
        	}
        }

        return serviceRegistry;
    }
 
}
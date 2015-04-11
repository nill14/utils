package com.github.nill14.utils.moduledi;

import java.util.Collection;
import java.util.Map;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IServiceContext;
import com.github.nill14.utils.init.api.IServiceRegistry;
import com.google.common.collect.Maps;

public class ServiceBuilder implements IServiceBuilder {

	private final Map<Class<?>, Class<?>> beans = Maps.newHashMap();
	private final IServiceContext context;
 	
	public ServiceBuilder(IServiceContext context) {
		this.context = context;
	}

	@Override
	public <S, T extends S> IServiceBuilder addBean(Class<T> impl, Class<S> iface) {
		beans.put(impl, iface);
		return this;
	}
	
	@Override
	public IServiceBuilder registerServices(IServiceRegistry serviceRegistry) {
		
		beans.forEach((serviceBean, service) -> 
			serviceRegistry.addService(serviceBean, context));
		
		return this;
	}
	
	@Override
	public IServiceBuilder buildDependencies(IDependencyDescriptorBuilder<Class<?>> dependencyBuilder) {
		
//		beans.keySet().stream()
//			.flatMap(bean -> new PojoInjectionDescriptor(bean).getMandatoryDependencies().stream())
//			.forEach(cls -> dependencyBuilder.uses(cls));
//		
//		beans.keySet().stream()
//			.flatMap(bean -> new PojoInjectionDescriptor(bean).getOptionalDependencies().stream())
//			.forEach(cls -> dependencyBuilder.usesOptionally(cls));
		
		beans.values().stream().forEach(iface -> dependencyBuilder.provides(iface));
		
		return this;
	}

	
	@Override
	public Collection<Class<?>> getBeans() {
		return beans.keySet();
	}
	
	
}

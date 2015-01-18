package com.github.nill14.utils.moduledi.module;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.AbstractModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.scope.ExtraScopesBeanPostProcessor;
import com.github.nill14.utils.moduledi.service.BreadService;
import com.github.nill14.utils.moduledi.service.IBreadService;

public class CustomerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IBreadService.class).to(BreadService.class);
	}

	@Override
	public void buildServices(IServiceBuilder builder) {
		builder.addBean(BreadService.class, IBreadService.class);
		
	}
	
	@Override
	protected void refreshContext(ClassPathXmlApplicationContext ctx, IServiceRegistry serviceRegistry) {
		ctx.addBeanFactoryPostProcessor(new ExtraScopesBeanPostProcessor(serviceRegistry));
		super.refreshContext(ctx, serviceRegistry);
	}
	
	
}

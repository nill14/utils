package com.github.nill14.utils.moduledi.module;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.AbstractModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.bean.customer.TaskBean;
import com.github.nill14.utils.moduledi.scope.ExtraScopesBeanPostProcessor;

public class CustomerModule extends AbstractModule {

	@Override
	protected void configure() {
	}

	@Override
	public void buildServices(IServiceBuilder builder) {
		
	}
	
	@Override
	protected void refreshContext(ClassPathXmlApplicationContext ctx, IServiceRegistry serviceRegistry) {
		ctx.addBeanFactoryPostProcessor(new ExtraScopesBeanPostProcessor(serviceRegistry));
		super.refreshContext(ctx, serviceRegistry);
		System.out.println(ctx.getBean(TaskBean.class).getAssignee());
		System.out.println(ctx.getBean(TaskBean.class).getReporter());
	}
	
	
	
}

package com.github.nill14.utils.moduledi.module;

import com.github.nill14.utils.moduledi.AbstractModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.bean.customer.ITaskService;
import com.github.nill14.utils.moduledi.bean.customer.TaskService;

public class CustomerModule extends AbstractModule {

	@Override
	protected void configure() {
	}

	@Override
	public void buildServices(IServiceBuilder builder) {
		builder.addBean(TaskService.class, ITaskService.class);
		
	}
	
//	@Override
//	protected void refreshContext(ClassPathXmlApplicationContext ctx, IServiceRegistry serviceRegistry) {
//		ctx.addBeanFactoryPostProcessor(new ExtraScopesBeanPostProcessor(serviceRegistry));
//		super.refreshContext(ctx, serviceRegistry);
//		
//		TaskBean taskBean = ctx.getBean(TaskBean.class);
//		Assert.assertEquals(Optional.empty(), taskBean.getAssignee());
//		Assert.assertEquals(Optional.of("reporter"), taskBean.getReporter());
//		
//	}
	
	
//	@Override
//	public void startModule(IServiceRegistry registry) {
//	}
	
	
	
}

package com.github.nill14.utils.moduledi.module;

import com.github.nill14.parsers.dependency.IDependencyDescriptorBuilder;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.Binder;
import com.github.nill14.utils.moduledi.IModule;
import com.github.nill14.utils.moduledi.bean.customer.ITaskService;
import com.github.nill14.utils.moduledi.bean.customer.TaskService;

public class CustomerModule implements IModule {

	@Override
	public void buildServices(Binder binder) {
		binder.bind(ITaskService.class).to(TaskService.class);
		
	}

	@Override
	public void startModule(IBeanInjector beanInjector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buildDependencies(IDependencyDescriptorBuilder<Class<?>> builder) {
		// TODO Auto-generated method stub
		
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

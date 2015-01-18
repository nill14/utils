package com.github.nill14.utils.moduledi.module;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.nill14.utils.init.api.IServiceRegistry;
import com.github.nill14.utils.moduledi.AbstractModule;
import com.github.nill14.utils.moduledi.IServiceBuilder;
import com.github.nill14.utils.moduledi.bean.snack.Snack;
import com.github.nill14.utils.moduledi.service.ISnackService;
import com.github.nill14.utils.moduledi.service.SnackService;

public class SnackModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ISnackService.class).to(SnackService.class);
	}

	@Override
	public void buildServices(IServiceBuilder builder) {
		builder.addBean(SnackService.class, ISnackService.class);
	}
	
	
	@Override
	protected void refreshContext(ClassPathXmlApplicationContext ctx, IServiceRegistry serviceRegistry) {
		super.refreshContext(ctx, serviceRegistry);
		System.out.println(ctx.getBean(Snack.class).getBread().getName());
		System.out.println(ctx.getBean(Snack.class).getSnackService());
	}
}

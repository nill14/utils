package com.github.nill14.utils.moduledi;

import org.testng.annotations.Test;

import com.github.nill14.utils.moduledi.module.BreadModule;
import com.github.nill14.utils.moduledi.module.DeliveryModule;
import com.github.nill14.utils.moduledi.module.SnackModule;
import com.github.nill14.utils.moduledi.service.ISnackService;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceTest {

	@Test
	public void test() {
	    /*
	     * Guice.createInjector() takes your Modules, and returns a new Injector
	     * instance. Most applications will call this method exactly once, in their
	     * main() method.
	     */
	    Injector injector = Guice.createInjector(new BreadModule(), new SnackModule(), new DeliveryModule());

	    /*
	     * Now that we've got the injector, we can build objects.
	     */
	    ISnackService snackService = injector.getInstance(ISnackService.class);
	    System.out.println(snackService);
	}
}

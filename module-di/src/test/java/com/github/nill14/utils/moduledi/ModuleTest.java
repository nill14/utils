package com.github.nill14.utils.moduledi;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ModuleTest {

	@Test
	public void test() {
	    /*
	     * Guice.createInjector() takes your Modules, and returns a new Injector
	     * instance. Most applications will call this method exactly once, in their
	     * main() method.
	     */
	    Injector injector = Guice.createInjector(new BreadModule(), new SnackModule());

	    /*
	     * Now that we've got the injector, we can build objects.
	     */
	    ISnackService snackService = injector.getInstance(ISnackService.class);
	    System.out.println(snackService);
	}
}

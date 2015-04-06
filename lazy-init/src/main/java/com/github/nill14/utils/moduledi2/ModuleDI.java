package com.github.nill14.utils.moduledi2;

import java.util.Arrays;

import com.github.nill14.utils.init.api.IBeanInjector;

public class ModuleDI {

	  public static IBeanInjector createBeanInjector(IModule... modules) {
	    return createBeanInjector(Arrays.asList(modules));
	  }

	  public static IBeanInjector createBeanInjector(Iterable<? extends IModule> modules) {
	    return new ModularBeanInjectorBuilder(modules).toBeanInjector();
	  }
	
}

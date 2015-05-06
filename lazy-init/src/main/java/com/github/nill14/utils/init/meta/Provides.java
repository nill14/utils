package com.github.nill14.utils.init.meta;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotates methods of a Module to create a provider method binding. The method's return
 * type is bound to its returned value. Framework will pass dependencies to the method as parameters.
 *
 */
@Documented 
@Target(METHOD) 
@Retention(RUNTIME)
public @interface Provides {}

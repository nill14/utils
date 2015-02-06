package com.github.nill14.utils.init.meta;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Target(FIELD)
@Retention(RUNTIME)
/**
 * 
 * Similar idea to IBeanInjector#wire(Class)
 *
 */
public @interface Wire {

}

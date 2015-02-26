package com.github.nill14.utils.init.meta;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RUNTIME)
/**
 * 
 * Similar idea to IBeanInjector#wire(Class).
 * Class annotated with @Wire can be injected even without being present in container.
 * In such case a new (prototype scope) instance is created and injected. 
 *
 */
public @interface Wire {

}

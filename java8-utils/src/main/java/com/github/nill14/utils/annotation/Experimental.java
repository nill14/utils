package com.github.nill14.utils.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * The API annotated with Experimental is subject to change and should not be widely used.
 * 
 * The API elements annotated with Experimental may be re-factored, removed or marked as stable.
 * Experimental is usually used with {@link Deprecated}
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RUNTIME)
@Documented
public @interface Experimental {

}

package com.github.nill14.utils.init.impl;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
/*package*/ @interface Counter {

    /** The name. */
    long value();
    
}

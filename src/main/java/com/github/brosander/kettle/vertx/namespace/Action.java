package com.github.brosander.kettle.vertx.namespace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by bryan on 4/19/15.
 */
@Target( { ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
public @interface Action {
}

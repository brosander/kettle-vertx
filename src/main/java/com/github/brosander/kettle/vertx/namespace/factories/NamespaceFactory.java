package com.github.brosander.kettle.vertx.namespace.factories;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import org.vertx.java.core.Vertx;

/**
 * Created by bryan on 4/18/15.
 */
public interface NamespaceFactory {
    Namespace create( Vertx vertx, String prefix, String name, Object object );
}

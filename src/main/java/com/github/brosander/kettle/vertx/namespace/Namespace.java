package com.github.brosander.kettle.vertx.namespace;

import org.vertx.java.core.Vertx;

/**
 * Created by bryan on 4/18/15.
 */
public interface Namespace extends ActionHandler, HasChildNamespaces {
    String getName();
    Vertx getVertx();
}

package com.github.brosander.kettle.vertx.namespace.factories;

import com.github.brosander.kettle.vertx.namespace.Namespace;

/**
 * Created by bryan on 4/18/15.
 */
public interface NamespaceFactory {
    Namespace create( String prefix, String name, Object object );
}

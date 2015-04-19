package com.github.brosander.kettle.vertx.namespace;

import java.util.Set;

/**
 * Created by bryan on 4/18/15.
 */
public interface HasChildNamespaces {
    Namespace getChild( String name );
    Set<String> getChildNames();
}

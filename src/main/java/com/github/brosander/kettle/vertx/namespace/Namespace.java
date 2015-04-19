package com.github.brosander.kettle.vertx.namespace;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.Set;

/**
 * Created by bryan on 4/18/15.
 */
public interface Namespace extends HasActions, HasChildNamespaces {
    String getName();
}

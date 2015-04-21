package com.github.brosander.kettle.vertx.namespace;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/19/15.
 */
public interface HasActions {
    boolean handle(Message<JsonObject> message) throws ActionException;
}

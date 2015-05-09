package com.github.brosander.kettle.vertx.namespace;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/18/15.
 */
public interface Namespace {
    String getAddress();

    boolean handle(Message<JsonObject> message) throws ActionException;

    void activate();

    void dispose();

    Vertx getVertx();
}

package com.github.brosander.kettle.vertx.namespace;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/18/15.
 */
public abstract class BaseNamespace implements HasChildNamespaces, Namespace {
    public static final String NO_HANDLER_FOUND_IN_NAMESPACE = "No handler found in namespace ";
    private final String prefix;
    private final String name;

    public BaseNamespace(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    protected String getChildPrefix() {
        if (name == null) {
            return null;
        }
        if (prefix == null) {
            return name;
        }
        return prefix + "." + name;
    }

    @Override
    public boolean handle(Message<JsonObject> message) throws ActionException{
        message.fail(405, NO_HANDLER_FOUND_IN_NAMESPACE + prefix + "." + name + " for message " + message.body().encodePrettily() );
        return true;
    }
}

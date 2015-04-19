package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.BaseNamespace;
import com.github.brosander.kettle.vertx.namespace.action.ActionHandler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/19/15.
 */
public abstract class ActionNamespace extends BaseNamespace {
    private final ActionHandler actionHandler;

    public ActionNamespace(String prefix, String name) {
        super(prefix, name);
        actionHandler = new ActionHandler(this);
    }

    @Override
    public boolean handle(Message<JsonObject> message) {
        return actionHandler.handle(message) || super.handle(message);
    }
}

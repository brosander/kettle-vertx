package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.BaseNamespace;
import com.github.brosander.kettle.vertx.namespace.ActionHandler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/21/15.
 */
public abstract class ActionNamespace extends BaseNamespace {
    private final ActionHandler actionHandler;

    public ActionNamespace(String prefix, String name, ActionHandler actionHandler) {
        super(prefix, name);
        this.actionHandler = actionHandler;
    }

    @Override
    public boolean handle(Message<JsonObject> message) throws ActionException {
        return actionHandler.handle(message) || super.handle(message);
    }
}

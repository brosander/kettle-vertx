package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.BaseNamespace;
import com.github.brosander.kettle.vertx.namespace.HasActions;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/21/15.
 */
public abstract class ActionNamespace extends BaseNamespace {
    private final HasActions hasActions;

    public ActionNamespace(String prefix, String name, HasActions hasActions) {
        super(prefix, name);
        this.hasActions = hasActions;
    }

    @Override
    public boolean handle(Message<JsonObject> message) throws ActionException {
        return hasActions.handle(message) || super.handle(message);
    }
}

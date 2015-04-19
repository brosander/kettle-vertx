package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.action.ActionHandler;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * Created by bryan on 4/18/15.
 */
public class ActionMapNamespace extends MapNamespace {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionMapNamespace.class);
    private final ActionHandler actionHandler;

    public ActionMapNamespace(String prefix, String name, Map map, NamespaceFactory namespaceFactory) {
        super(prefix, name, map, namespaceFactory);
        this.actionHandler = new ActionHandler(this);
    }

    @Override
    public boolean handle(Message<JsonObject> message) {
        return actionHandler.handle(message) || super.handle(message);
    }
}

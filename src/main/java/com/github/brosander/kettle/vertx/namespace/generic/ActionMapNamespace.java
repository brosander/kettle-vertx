package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.ActionHandler;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * Created by bryan on 4/18/15.
 */
public class ActionMapNamespace extends MapNamespace {
    private ActionHandler actionHandler;

    public ActionMapNamespace(Vertx vertx, String prefix, String name, Map map, NamespaceFactory namespaceFactory) {
        this(vertx, prefix, name, map, namespaceFactory, null);
    }

    public ActionMapNamespace(Vertx vertx, String prefix, String name, Map map, NamespaceFactory namespaceFactory, ActionHandler actionHandler) {
        super(vertx, prefix, name, map, namespaceFactory);
        this.actionHandler = actionHandler;
    }

    public void setActionHandler(ActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    public boolean handle(Message<JsonObject> message) throws ActionException {
        return actionHandler.handle(message) || super.handle(message);
    }
}

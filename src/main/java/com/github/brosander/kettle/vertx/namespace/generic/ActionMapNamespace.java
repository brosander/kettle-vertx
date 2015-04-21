package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.HasActions;
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
    private final HasActions actionHandler;

    public ActionMapNamespace(String prefix, String name, Map map, NamespaceFactory namespaceFactory, HasActions hasActions) {
        super(prefix, name, map, namespaceFactory);
        this.actionHandler = hasActions;
    }

    @Override
    public boolean handle(Message<JsonObject> message) throws ActionException {
        return actionHandler.handle(message) || super.handle(message);
    }
}

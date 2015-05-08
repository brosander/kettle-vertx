package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.ActionHandler;
import com.github.brosander.kettle.vertx.namespace.factories.BeanMethodMapping;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/21/15.
 */
public class ActionBeanNamespace extends BeanNamespace {
    private final ActionHandler actionHandler;

    public ActionBeanNamespace(Vertx vertx, String prefix, String name, Object object, NamespaceFactory namespaceFactory, BeanMethodMapping beanMethodMapping, ActionHandler actionHandler) {
        super(vertx, prefix, name, object, namespaceFactory, beanMethodMapping);
        this.actionHandler = actionHandler;
    }

    @Override
    public boolean handle(Message<JsonObject> message) throws ActionException {
        return actionHandler.handle(message) || super.handle(message);
    }
}

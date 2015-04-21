package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.HasActions;
import com.github.brosander.kettle.vertx.namespace.factories.BeanMethodMapping;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/21/15.
 */
public class ActionBeanNamespace extends BeanNamespace {
    private final HasActions hasActions;

    public ActionBeanNamespace(String prefix, String name, Object object, NamespaceFactory namespaceFactory, BeanMethodMapping beanMethodMapping, HasActions hasActions) {
        super(prefix, name, object, namespaceFactory, beanMethodMapping);
        this.hasActions = hasActions;
    }

    @Override
    public boolean handle(Message<JsonObject> message) throws ActionException {
        return hasActions.handle(message) || super.handle(message);
    }
}

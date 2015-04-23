package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.ActionHandler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 4/21/15.
 */
public class ActionHandlerMap implements ActionHandler {
    private final Map<String, ActionHandler> actionHandlerMap;

    protected ActionHandlerMap(Map<String, ActionHandler> actionHandlerMap) {
        this.actionHandlerMap = actionHandlerMap;
    }

    @Override
    public boolean handle(Message<JsonObject> message) throws ActionException {
        String action = message.body().getString("action");
        ActionHandler actionHandler = actionHandlerMap.get(action);
        if (actionHandler != null) {
            return actionHandler.handle(message);
        }
        return false;
    }

    public static class Builder {
        private final Map<String, ActionHandler> actionHandlerMap = new HashMap<>();

        public Builder addActionHandler(String name, ActionHandler actionHandler) {
            actionHandlerMap.put(name, actionHandler);
            return this;
        }

        public ActionHandlerMap build() {
            return new ActionHandlerMap(new HashMap<>(actionHandlerMap));
        }
    }
}

package com.github.brosander.kettle.vertx.namespace.action;

import com.github.brosander.kettle.vertx.namespace.Action;
import com.github.brosander.kettle.vertx.namespace.HasActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 4/19/15.
 */
public class ActionHandler implements HasActions {
    public static final String NAME = "name";
    public static final String MUST_SPECIFY_NAME_TO_OPERATE_ON = "Must specify name to operate on";
    public static final String ACTION = "action";
    public static final String MUST_SPECIFY_AN_ACTION = "Must specify an action";
    public static final String INVALID_ACTION = "Invalid action";
    public static final String UNABLE_TO_INVOKE_ACTION_METHOD = "Unable to invoke action method";
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionHandler.class);
    private final HasActions parent;
    private final Map<String, Method> actions;
    private boolean first = true;

    public ActionHandler(HasActions parent) {
        this.parent = parent;
        actions = new HashMap<>();
    }

    private void init() {
        for (Method method : parent.getClass().getMethods()) {
            if (method.getAnnotation(Action.class) != null) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes == null || parameterTypes.length != 2 || parameterTypes[0] != String.class || parameterTypes[1] != Message.class) {
                    LOGGER.warn("Unable to add action method " + method.toString() + " expecting 2 params, first String, second Message");
                    continue;
                }
                actions.put(method.getName(), method);
            }
        }
    }

    @Override
    public boolean handle(Message<JsonObject> message) {
        if (first) {
            init();
            first = false;
        }
        JsonObject body = message.body();
        String name = body.getString(NAME);
        if (name == null || name.length() == 0) {
            message.fail(400, MUST_SPECIFY_NAME_TO_OPERATE_ON);
            return true;
        }
        String action = body.getString(ACTION);
        if (action == null || action.length() == 0) {
            message.fail(400, MUST_SPECIFY_AN_ACTION);
            return true;
        }
        Method method = actions.get(action);
        if (method == null) {
            message.fail(400, INVALID_ACTION);
            return true;
        }
        try {
            method.invoke(parent, name, message);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            message.fail(500, UNABLE_TO_INVOKE_ACTION_METHOD);
            return true;
        } catch (InvocationTargetException e) {
            message.fail(500, e.getMessage());
            return true;
        }
        return false;
    }
}

package com.github.brosander.kettle.vertx.namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/18/15.
 */
public class BaseNamespace implements Namespace {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseNamespace.class.getCanonicalName());
    public static final String NO_HANDLER_FOUND_AT_ADDRESS = "No handler found at address ";
    public static final String FOR_MESSAGE = " for message ";
    private final String address;
    private final Vertx vertx;
    private final Handler<Message> handler;
    private final String noHanderlMessage;

    public BaseNamespace(Vertx vertx, String prefix, String name) {
        this.vertx = vertx;
        this.address = makeChildAddress(prefix, name);
        if (this.address != null) {
            handler = new Handler<Message>() {
                @Override
                public void handle(Message event) {
                    try {
                        BaseNamespace.this.handle(event);
                    } catch (ActionException e) {
                        event.fail(e.getCode(), e.getMessage());
                    }
                }
            };
        } else {
            handler = null;
        }
        this.noHanderlMessage = new StringBuilder(NO_HANDLER_FOUND_AT_ADDRESS).append(address).append(FOR_MESSAGE).toString();
    }

    protected String makeChildAddress(String prefix, String name) {
        if (name == null) {
            return null;
        } else if (prefix == null) {
            return name;
        } else {
            return prefix + "." + name;
        }
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void activate() {
        if (address != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Registering handler at address: " + address);
            }
            vertx.eventBus().registerHandler(address, handler);
        }
    }

    @Override
    public void dispose() {
        if (address != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unregistering handler at address: " + address);
            }
            vertx.eventBus().unregisterHandler(address, handler);
        }
    }

    @Override
    public boolean handle(Message<JsonObject> message) throws ActionException {
        message.fail(405, noHanderlMessage + message.body().encodePrettily());
        return true;
    }

    @Override
    public Vertx getVertx() {
        return vertx;
    }
}

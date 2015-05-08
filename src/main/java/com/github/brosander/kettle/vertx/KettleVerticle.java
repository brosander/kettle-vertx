package com.github.brosander.kettle.vertx;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.kettle.RootNamespace;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.HashMap;

/**
 * Created by bryan on 4/18/15.
 */
public class KettleVerticle extends Verticle {
    public static final String KETTLE_VERTICLE = "kettle-verticle";
    private Namespace rootNamespace;

    public KettleVerticle() {
        this(null);
    }

    public KettleVerticle(Namespace rootNamespace) {
        super();
        this.rootNamespace = rootNamespace;
    }

    @Override
    public void start(Future<Void> startedResult) {
        if (rootNamespace == null) {
            rootNamespace = new RootNamespace(vertx, new HashMap<String, Object>());
        }
        try {
            KettleClientEnvironment.init();
            KettleEnvironment.init();
            vertx.eventBus().registerHandler(KETTLE_VERTICLE, new Handler<Message<JsonObject>>() {
                @Override
                public void handle(Message<JsonObject> event) {
                    JsonObject jsonObject = event.body();
                    Namespace messageNamespace = rootNamespace;
                    JsonArray eventNamespace = jsonObject.getArray("namespace");
                    if (eventNamespace == null) {
                        event.fail(400, "namespace required");
                    } else {
                        for (Object namespace : eventNamespace) {
                            messageNamespace = messageNamespace.getChild(String.valueOf(namespace));
                            if (messageNamespace == null) {
                                event.fail(404, "Unable to locate namespace corresponding to " + eventNamespace);
                                return;
                            }
                        }
                    }
                    try {
                        messageNamespace.handle(event);
                    } catch (ActionException e) {
                        event.fail(e.getCode(), e.getMessage());
                    }
                }
            });
        } catch (KettleException e) {
            startedResult.setFailure(e);
        }
        startedResult.setResult(null);
    }
}

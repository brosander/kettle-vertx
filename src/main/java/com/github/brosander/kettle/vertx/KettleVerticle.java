package com.github.brosander.kettle.vertx;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.BeanMethodMapping;
import com.github.brosander.kettle.vertx.namespace.factories.BeanNamespaceFactoryDelegate;
import com.github.brosander.kettle.vertx.namespace.factories.MapNamespaceFactoryDelegate;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactoryDelegate;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactoryManager;
import com.github.brosander.kettle.vertx.namespace.kettle.StepMetasNamespaceFactoryDelegate;
import com.github.brosander.kettle.vertx.namespace.kettle.TransMetasNamespaceFactoryDelegate;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.TransMeta;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by bryan on 4/18/15.
 */
public class KettleVerticle extends Verticle {
    public static final String KETTLE_VERTICLE = "kettle-verticle";
    private final Namespace rootNamespace;

    public KettleVerticle() {
        this(buildNamespace());
    }

    public KettleVerticle(Namespace rootNamespace) {
        this.rootNamespace = rootNamespace;
    }

    private static Namespace buildNamespace() {
        ArrayList<NamespaceFactoryDelegate> kettleNamespaceFactoryDelegates = new ArrayList<>();
        NamespaceFactoryManager namespaceFactoryManager = new NamespaceFactoryManager(kettleNamespaceFactoryDelegates);
        ArrayList<BeanMethodMapping> beanMethodMappings = new ArrayList<BeanMethodMapping>(Arrays.<BeanMethodMapping>asList(
                new BeanMethodMapping(TransMeta.class, new HashSet<String>(), new HashSet<String>(Arrays.asList(StepMetasNamespaceFactoryDelegate.STEP_METAS)))));
        kettleNamespaceFactoryDelegates.add(new StepMetasNamespaceFactoryDelegate(namespaceFactoryManager));
        kettleNamespaceFactoryDelegates.add(new TransMetasNamespaceFactoryDelegate(namespaceFactoryManager));
        kettleNamespaceFactoryDelegates.add(new MapNamespaceFactoryDelegate(namespaceFactoryManager));
        kettleNamespaceFactoryDelegates.add(new BeanNamespaceFactoryDelegate(namespaceFactoryManager, beanMethodMappings));
        Map<String, Object> rootNamespaceMap = new HashMap<>();
        rootNamespaceMap.put("transMetas", new HashMap<String, TransMeta>());
        rootNamespaceMap.put("jobMetas", new HashMap<String, JobMeta>());
        return namespaceFactoryManager.create(null, null, rootNamespaceMap);
    }

    @Override
    public void start(Future<Void> startedResult) {
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
                    messageNamespace.handle(event);
                }
            });
        } catch (KettleException e) {
            startedResult.setFailure(e);
        }
        startedResult.setResult(null);
    }
}

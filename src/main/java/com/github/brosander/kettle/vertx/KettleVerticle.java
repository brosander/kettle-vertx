package com.github.brosander.kettle.vertx;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.kettle.RootNamespace;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.vertx.java.core.Future;
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
            rootNamespace = new RootNamespace(vertx, KETTLE_VERTICLE, new HashMap<String, Object>());
        }
        try {
            KettleClientEnvironment.init();
            KettleEnvironment.init();
            rootNamespace.activate();
        } catch (KettleException e) {
            startedResult.setFailure(e);
        }
        startedResult.setResult(null);
    }

    @Override
    public void stop() {
        if (rootNamespace != null) {
            rootNamespace.dispose();
        }
    }
}

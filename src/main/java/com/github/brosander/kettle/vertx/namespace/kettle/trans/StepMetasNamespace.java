package com.github.brosander.kettle.vertx.namespace.kettle.trans;

import com.github.brosander.kettle.vertx.namespace.HasChildNamespaces;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.ActionHandlerMap;
import com.github.brosander.kettle.vertx.namespace.generic.ActionNamespace;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.vertx.java.core.Vertx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 4/19/15.
 */
public class StepMetasNamespace extends ActionNamespace implements HasChildNamespaces {
    private final TransMeta transMeta;
    private final Map<String, Namespace> childNamespaces;
    private final NamespaceFactory namespaceFactory;

    public StepMetasNamespace(Vertx vertx, String prefix, String name, TransMeta transMeta) {
        super(vertx, prefix, name, new ActionHandlerMap.Builder().build());
        this.transMeta = transMeta;
        this.namespaceFactory = new StepMetaNamespace.Factory();
        childNamespaces = new HashMap<>();
    }

    @Override
    public void activate() {
        super.activate();
        Vertx vertx = getVertx();
        String address = getAddress();
        for (StepMeta stepMeta : transMeta.getSteps()) {
            String name = stepMeta.getName();
            childNamespaces.put(name, namespaceFactory.create(vertx, address, name, stepMeta));
        }
        for (Namespace namespace : childNamespaces.values()) {
            namespace.activate();
        }
    }

    @Override
    public void dispose() {
        for (Namespace namespace : childNamespaces.values()) {
            namespace.dispose();
        }
        childNamespaces.clear();
        super.dispose();
    }

    @Override
    public Set<String> getChildAddresses() {
        List<StepMeta> steps = transMeta.getSteps();
        Set<String> stepNames = new HashSet<>(steps.size());
        for (StepMeta step : steps) {
            stepNames.add(step.getName());
        }
        return stepNames;
    }

    public static class Factory implements NamespaceFactory {

        @Override
        public Namespace create(Vertx vertx, String prefix, String name, Object object) {
            return new StepMetasNamespace(vertx, prefix, name, (TransMeta) object);
        }
    }
}

package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.ActionHandlerMap;
import com.github.brosander.kettle.vertx.namespace.generic.ActionNamespace;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.vertx.java.core.Vertx;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bryan on 4/19/15.
 */
public class StepMetasNamespace extends ActionNamespace {
    private final TransMeta transMeta;
    private final NamespaceFactory namespaceFactory;

    public StepMetasNamespace(Vertx vertx, String prefix, String name, TransMeta transMeta) {
        super(vertx, prefix, name, new ActionHandlerMap.Builder().build());
        this.transMeta = transMeta;
        this.namespaceFactory = new StepMetaNamespace.Factory();
    }

    @Override
    public Namespace getChild(String name) {
        for (StepMeta stepMeta : transMeta.getSteps()) {
            if (name.equals(stepMeta.getName())) {
                return namespaceFactory.create(getVertx(), getChildPrefix(), name, stepMeta);
            }
        }
        return null;
    }

    @Override
    public Set<String> getChildNames() {
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

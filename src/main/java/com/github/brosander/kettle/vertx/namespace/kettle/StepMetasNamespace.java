package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.Action;
import com.github.brosander.kettle.vertx.namespace.BaseNamespace;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.ActionNamespace;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bryan on 4/19/15.
 */
public class StepMetasNamespace extends ActionNamespace {
    private final TransMeta transMeta;
    private final NamespaceFactory namespaceFactory;

    public StepMetasNamespace(String prefix, String name, TransMeta transMeta, NamespaceFactory namespaceFactory) {
        super(prefix, name);
        this.transMeta = transMeta;
        this.namespaceFactory = namespaceFactory;
    }

    @Override
    public Namespace getChild(String name) {
        for (StepMeta stepMeta : transMeta.getSteps()) {
            if (name.equals(stepMeta.getName())) {
                return namespaceFactory.create(getChildPrefix(), name, stepMeta);
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

    @Action
    public void create(String stepMetaName, Message<JsonObject> message) {

    }
}

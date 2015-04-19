package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactoryDelegate;
import org.pentaho.di.trans.TransMeta;

/**
 * Created by bryan on 4/19/15.
 */
public class StepMetasNamespaceFactoryDelegate implements NamespaceFactoryDelegate {
    public static final String STEP_METAS = "stepMetas";
    private final NamespaceFactory namespaceFactory;

    public StepMetasNamespaceFactoryDelegate(NamespaceFactory namespaceFactory) {
        this.namespaceFactory = namespaceFactory;
    }

    @Override
    public boolean handles(String prefix, String name, Class<?> clazz) {
        return TransMeta.class.isAssignableFrom(clazz) && STEP_METAS.equals(name);
    }

    @Override
    public Namespace create(String prefix, String name, Object object) {
        return new StepMetasNamespace(prefix, name, (TransMeta) object, namespaceFactory);
    }
}

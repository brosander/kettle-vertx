package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactoryDelegate;
import org.pentaho.di.trans.TransMeta;

import java.util.Map;

/**
 * Created by bryan on 4/18/15.
 */
public class TransMetasNamespaceFactoryDelegate implements NamespaceFactoryDelegate {
    public static final String TRANS_METAS = "transMetas";
    private final NamespaceFactory namespaceFactory;

    public TransMetasNamespaceFactoryDelegate(NamespaceFactory namespaceFactory) {
        this.namespaceFactory = namespaceFactory;
    }

    @Override
    public boolean handles(String prefix, String name, Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz) && TRANS_METAS.equals(name);
    }

    @Override
    public Namespace create(String prefix, String name, Object object) {
        final Map<String, TransMeta> transMetaMap = (Map<String, TransMeta>) object;
        return new TransMetasActionMapNamespace(prefix, name, transMetaMap, namespaceFactory);
    }
}

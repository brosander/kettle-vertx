package com.github.brosander.kettle.vertx.namespace.factories;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.generic.MapNamespace;

import java.util.Map;

/**
 * Created by bryan on 4/18/15.
 */
public class MapNamespaceFactoryDelegate implements NamespaceFactoryDelegate {
    private final NamespaceFactory namespaceFactory;

    public MapNamespaceFactoryDelegate(NamespaceFactory namespaceFactory) {
        this.namespaceFactory = namespaceFactory;
    }

    @Override
    public boolean handles(String prefix, String name, Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    @Override
    public Namespace create(String prefix, String name, Object object) {
        return new MapNamespace(prefix, name, (Map) object, namespaceFactory);
    }
}

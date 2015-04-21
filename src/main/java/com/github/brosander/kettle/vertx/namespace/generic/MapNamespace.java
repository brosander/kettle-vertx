package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.BaseNamespace;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 4/18/15.
 */
public class MapNamespace extends BaseNamespace {
    private final Map map;
    private final NamespaceFactory namespaceFactory;
    public MapNamespace(String prefix, String name, Map map, NamespaceFactory namespaceFactory) {
        super(prefix, name);
        this.map = map;
        this.namespaceFactory = namespaceFactory;
    }

    @Override
    public Namespace getChild(String name) {
        Object object = map.get(name);
        if (object == null) {
            return null;
        }
        return namespaceFactory.create(getChildPrefix(), name, object);
    }

    @Override
    public Set<String> getChildNames() {
        return Collections.unmodifiableSet(map.keySet());
    }
}
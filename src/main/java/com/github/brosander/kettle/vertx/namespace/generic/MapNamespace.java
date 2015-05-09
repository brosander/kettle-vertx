package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.BaseNamespace;
import com.github.brosander.kettle.vertx.namespace.HasChildNamespaces;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Vertx;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 4/18/15.
 */
public class MapNamespace extends BaseNamespace implements HasChildNamespaces {
    private final Map<String, Object> map;
    private final Map<String, Namespace> childNamespaces;
    private final NamespaceFactory namespaceFactory;

    public MapNamespace(Vertx vertx, String prefix, String name, Map<String, Object> map, NamespaceFactory namespaceFactory) {
        super(vertx, prefix, name);
        this.map = map;
        this.namespaceFactory = namespaceFactory;
        childNamespaces = new HashMap<>();
    }

    @Override
    public void activate() {
        super.activate();
        String address = getAddress();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String name = entry.getKey();
            childNamespaces.put(name, namespaceFactory.create(getVertx(), address, name, entry.getValue()));
        }
        for (Map.Entry<String, Namespace> entry : childNamespaces.entrySet()) {
            entry.getValue().activate();
        }
    }

    public boolean put(String name, Object value) {
        map.put(name, value);
        String address = getAddress();
        Namespace namespace = namespaceFactory.create(getVertx(), address, name, value);
        Namespace displacedNamespace = childNamespaces.put(name, namespace);
        boolean existed = displacedNamespace != null;
        if (existed) {
            displacedNamespace.dispose();
        }
        namespace.activate();
        return existed;
    }

    protected boolean remove(String name) {
        map.remove(name);
        Namespace remove = childNamespaces.remove(name);
        if (remove != null) {
            remove.dispose();
            return true;
        }
        return false;
    }

    public boolean containsKey(String name) {
        return childNamespaces.containsKey(name);
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
        return Collections.unmodifiableSet(childNamespaces.keySet());
    }
}

package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.BaseNamespace;
import com.github.brosander.kettle.vertx.namespace.HasChildNamespaces;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.BeanMethodMapping;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Vertx;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 4/18/15.
 */
public class BeanNamespace extends BaseNamespace implements HasChildNamespaces {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanNamespace.class);
    private final Object object;
    private final Set<String> selfManagingProperties;
    private final Map<String, Method> propertyMap;
    private final Map<String, Namespace> childNamespaces;
    private final NamespaceFactory namespaceFactory;

    public BeanNamespace(Vertx vertx, String prefix, String name, Object object, NamespaceFactory namespaceFactory, BeanMethodMapping beanMethodMapping) {
        super(vertx, prefix, name);
        this.object = object;
        this.selfManagingProperties = new HashSet<>(beanMethodMapping.getSelfManagingProperties());
        this.namespaceFactory = namespaceFactory;
        this.propertyMap = new HashMap<>(beanMethodMapping.getMethodMap());
        childNamespaces = new HashMap<>();
    }

    @Override
    public void activate() {
        super.activate();
        Vertx vertx = getVertx();
        String address = getAddress();
        for (String selfManagingProperty : selfManagingProperties) {
            childNamespaces.put(selfManagingProperty, namespaceFactory.create(vertx, address, selfManagingProperty, object));
        }
        for (Map.Entry<String, Method> methodEntry : propertyMap.entrySet()) {
            String name = methodEntry.getKey();
            if (!childNamespaces.containsKey(name)) {
                Method method = methodEntry.getValue();
                try {
                    childNamespaces.put(name, namespaceFactory.create(vertx, address, name, method.invoke(object)));
                } catch (Exception e) {
                    LOGGER.error("Unable to invoke " + method + " on " + object);
                }
            }
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
        return Collections.unmodifiableSet(childNamespaces.keySet());
    }
}

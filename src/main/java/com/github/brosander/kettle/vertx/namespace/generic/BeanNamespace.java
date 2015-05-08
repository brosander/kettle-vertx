package com.github.brosander.kettle.vertx.namespace.generic;

import com.github.brosander.kettle.vertx.namespace.BaseNamespace;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.BeanMethodMapping;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Vertx;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 4/18/15.
 */
public class BeanNamespace extends BaseNamespace {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanNamespace.class);
    private final Object object;
    private final Set<String> selfManagingProperties;
    private final Map<String, Method> propertyMap;
    private final NamespaceFactory namespaceFactory;

    public BeanNamespace(Vertx vertx, String prefix, String name, Object object, NamespaceFactory namespaceFactory, BeanMethodMapping beanMethodMapping) {
        super(vertx, prefix, name);
        this.object = object;
        this.selfManagingProperties = beanMethodMapping.getSelfManagingProperties();
        this.namespaceFactory = namespaceFactory;
        this.propertyMap = new HashMap<>(beanMethodMapping.getMethodMap());
    }

    @Override
    public Namespace getChild(String name) {
        if (object == null) {
            return null;
        }
        if (selfManagingProperties.contains(name)) {
            return namespaceFactory.create(getVertx(), getChildPrefix(), name, object);
        }
        Method getter = propertyMap.get(name);
        if (getter == null) {
            return null;
        }
        try {
            return namespaceFactory.create(getVertx(), getChildPrefix(), name, getter.invoke(object));
        } catch (Exception e) {
            LOGGER.error("Unable to invoke " + getter + " on " + object);
        }
        return null;
    }

    @Override
    public Set<String> getChildNames() {
        return Collections.unmodifiableSet(propertyMap.keySet());
    }
}

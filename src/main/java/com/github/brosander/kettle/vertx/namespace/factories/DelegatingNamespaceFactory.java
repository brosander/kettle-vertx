package com.github.brosander.kettle.vertx.namespace.factories;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import org.vertx.java.core.Vertx;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 4/21/15.
 */
public class DelegatingNamespaceFactory implements NamespaceFactory {
    private final Map<String, NamespaceFactory> delegates;

    public DelegatingNamespaceFactory(Map<String, NamespaceFactory> delegates) {
        this.delegates = delegates;
    }

    @Override
    public Namespace create(Vertx vertx, String prefix, String name, Object object) {
        NamespaceFactory namespaceFactory = delegates.get(name);
        if (namespaceFactory == null) {
            return null;
        }
        return namespaceFactory.create(vertx, prefix, name, object);
    }

    public static class Builder {
        private final Map<String, NamespaceFactory> delegates = new HashMap<>();

        public Builder addDelegate(String name, NamespaceFactory namespaceFactory) {
            delegates.put(name, namespaceFactory);
            return this;
        }

        public DelegatingNamespaceFactory build() {
            return new DelegatingNamespaceFactory(new HashMap<>(delegates));
        }
    }
}

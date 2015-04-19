package com.github.brosander.kettle.vertx.namespace.factories;

/**
 * Created by bryan on 4/18/15.
 */
public interface NamespaceFactoryDelegate extends NamespaceFactory {
    boolean handles(String prefix, String name, Class<?> clazz);
}

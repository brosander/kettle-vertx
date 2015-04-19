package com.github.brosander.kettle.vertx.namespace.factories;

import com.github.brosander.kettle.vertx.namespace.Namespace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 4/18/15.
 */
public class NamespaceFactoryManager implements NamespaceFactory {
    public final Map<String, Map<String, Map<Class<?>, NamespaceFactory>>> namespaceFactoryMap = new HashMap<>();
    public final List<NamespaceFactoryDelegate> kettleNamespaceFactoryDelegates;

    public NamespaceFactoryManager(List<NamespaceFactoryDelegate> kettleNamespaceFactoryDelegates) {
        this.kettleNamespaceFactoryDelegates = kettleNamespaceFactoryDelegates;
    }

    private NamespaceFactory getNamespaceFactory(String prefix, String name, Class<?> clazz) {
        Map<String, Map<Class<?>, NamespaceFactory>> nameClassMap = namespaceFactoryMap.get(prefix);
        if (nameClassMap == null) {
            nameClassMap = new HashMap<>();
            namespaceFactoryMap.put(prefix, nameClassMap);
        }
        Map<Class<?>, NamespaceFactory> classMap = nameClassMap.get(name);
        if (classMap == null) {
            classMap = new HashMap<>();
            nameClassMap.put(name, classMap);
        }
        return classMap.get(clazz);
    }

    @Override
    public Namespace create(String prefix, String name, Object object) {
        Class<?> clazz = object == null ? null : object.getClass();
        NamespaceFactory namespaceFactory = getNamespaceFactory(prefix, name, clazz);
        if (namespaceFactory == null) {
            for (NamespaceFactoryDelegate kettleNamespaceFactoryDelegate : kettleNamespaceFactoryDelegates) {
                if (kettleNamespaceFactoryDelegate.handles(prefix, name, clazz)) {
                    namespaceFactoryMap.get(prefix).get(name).put(clazz, kettleNamespaceFactoryDelegate);
                    namespaceFactory = kettleNamespaceFactoryDelegate;
                    break;
                }
            }
        }
        return namespaceFactory == null ? null : namespaceFactory.create(prefix, name, object);
    }
}

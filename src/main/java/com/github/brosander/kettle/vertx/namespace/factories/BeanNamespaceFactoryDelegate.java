package com.github.brosander.kettle.vertx.namespace.factories;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.generic.BeanNamespace;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 4/18/15.
 */
public class BeanNamespaceFactoryDelegate implements NamespaceFactoryDelegate {
    private final NamespaceFactory namespaceFactory;
    private final List<BeanMethodMapping> beanMethodMappings;
    private final Map<Class, Map<String, Method>> processedMapping;

    public BeanNamespaceFactoryDelegate(NamespaceFactory namespaceFactory, List<BeanMethodMapping> beanMethodMappings) {
        this.namespaceFactory = namespaceFactory;
        this.beanMethodMappings = beanMethodMappings;
        processedMapping = new HashMap<>();
    }

    @Override
    public boolean handles(String prefix, String name, Class<?> clazz) {
        return true;
    }

    @Override
    public Namespace create(String prefix, String name, Object object) {
        Class clazz = object.getClass();
        Set<String> selfManagingProperties = new HashSet<>();
        Map<String, Method> methodMap = processedMapping.get(clazz);
        if (methodMap == null) {
            methodMap = new HashMap<>();
            for (BeanMethodMapping beanMethodMapping : beanMethodMappings) {
                if (beanMethodMapping.getClazz().isAssignableFrom(clazz)) {
                    selfManagingProperties.addAll(beanMethodMapping.getSelfManagingProperties());
                    methodMap.putAll(beanMethodMapping.getMethodMap());
                }
            }
        }
        return new BeanNamespace(prefix, name, object, selfManagingProperties, methodMap, namespaceFactory);
    }
}

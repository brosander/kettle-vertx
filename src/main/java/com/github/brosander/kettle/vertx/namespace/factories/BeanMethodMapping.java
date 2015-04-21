package com.github.brosander.kettle.vertx.namespace.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 4/18/15.
 */
public class BeanMethodMapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanMethodMapping.class);
    private final Class clazz;
    private final Set<String> selfManagingProperties;
    private final Map<String, Method> methodMap;

    public BeanMethodMapping(Class clazz, Set<String> propertyNames, Map<String, String> propertyNameToMethodMap, Set<String> selfManagingProperties) {
        this.clazz = clazz;
        this.selfManagingProperties = selfManagingProperties;
        this.methodMap = getMethods(clazz, propertyNames, propertyNameToMethodMap);
    }

    private static Map<String, Method> getMethods(Class<?> clazz, Set<String> propertyNames, Map<String, String> propertyNameToMethodMap) {
        Map<String, Method> methodMap = new HashMap<>();
        for (String propertyName : propertyNames) {
            if (!propertyNameToMethodMap.containsKey(propertyName)) {
                StringBuilder stringBuilder = new StringBuilder("get");
                stringBuilder.append(propertyName.substring(0, 1).toUpperCase());
                if (propertyName.length() > 1) {
                    stringBuilder.append(propertyName.substring(1));
                }
                propertyNameToMethodMap.put(propertyName, stringBuilder.toString());
            }
        }
        for (Map.Entry<String, String> entry : propertyNameToMethodMap.entrySet()) {
            String propertyName = entry.getKey();
            String methodName = entry.getValue();
            try {
                methodMap.put(propertyName, clazz.getMethod(methodName));
            } catch (NoSuchMethodException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Getter " + methodName + " for property " + propertyName + " not found");
                }
            }
        }
        return methodMap;
    }

    public Class getClazz() {
        return clazz;
    }

    public Map<String, Method> getMethodMap() {
        return methodMap;
    }

    public Set<String> getSelfManagingProperties() {
        return selfManagingProperties;
    }

    public static class Builder {
        private final Class clazz;
        private final Set<String> properties = new HashSet<>();
        private final Set<String> selfManagingProperties = new HashSet<>();
        private final Map<String, String> methodMap = new HashMap<>();

        public Builder(Class clazz) {
            this.clazz = clazz;
        }

        public Builder addProperty(String name) {
            properties.add(name);
            return this;
        }

        public Builder addSelfManagingProperty(String name) {
            selfManagingProperties.add(name);
            properties.add(name);
            return this;
        }

        public Builder addGetterMethod(String name, String methodName) {
            methodMap.put(name, methodName);
            return this;
        }

        public BeanMethodMapping build() {
            return new BeanMethodMapping(clazz, new HashSet<>(properties), new HashMap<>(methodMap), new HashSet<>(selfManagingProperties));
        }
    }
}

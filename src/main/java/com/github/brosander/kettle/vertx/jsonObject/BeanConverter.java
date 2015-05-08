package com.github.brosander.kettle.vertx.jsonObject;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 5/8/15.
 */
public class BeanConverter<T> {
    public static final String GET = "get";
    public static final int GET_LENGTH = GET.length();
    private static final Set<Class<?>> acceptableClasses = getAcceptableClasses();
    private final Class<T> clazz;
    private final Map<String, Method> beanMethods;

    public BeanConverter(Class<T> clazz, Map<String, Method> beanMethods) {
        this.clazz = clazz;
        this.beanMethods = beanMethods;
    }

    private static Set<Class<?>> getAcceptableClasses() {
        Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(JsonObject.class);
        result.add(JsonArray.class);
        result.add(String.class);
        result.add(Number.class);
        result.add(int.class);
        result.add(float.class);
        result.add(double.class);
        result.add(long.class);
        result.add(Boolean.class);
        result.add(boolean.class);
        result.add(byte[].class);
        return result;
    }

    private static boolean isAcceptable(Class<?> clazz) {
        for (Class<?> acceptableClass : acceptableClasses) {
            if (acceptableClass.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    public static <T> BeanConverter<T> forClass(Class<T> clazz) {
        return forClass(clazz, new HashSet<String>());
    }

    public static <T> BeanConverter<T> forClass(Class<T> clazz, Set<String> ignoredMethods) {
        Map<String, Method> beanMethods = new HashMap<String, Method>();
        for (Method method : clazz.getMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length == 0) {
                String methodName = method.getName();
                if (!ignoredMethods.contains(methodName) && methodName.startsWith(GET) && methodName.length() > GET_LENGTH && isAcceptable(method.getReturnType())) {
                    StringBuilder name = new StringBuilder(methodName.substring(GET_LENGTH, GET_LENGTH + 1).toLowerCase());
                    if (methodName.length() > GET_LENGTH + 1) {
                        name.append(methodName.substring(GET_LENGTH + 1));
                    }
                    beanMethods.put(name.toString(), method);
                }
            }
        }
        return new BeanConverter(clazz, beanMethods);
    }

    public JsonObject convert(T object) {
        if (object == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, Method> stringMethodEntry : beanMethods.entrySet()) {
            try {
                jsonObject.putValue(stringMethodEntry.getKey(), stringMethodEntry.getValue().invoke(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
}

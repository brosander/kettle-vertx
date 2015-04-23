package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.factories.DelegatingNamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.MapNamespace;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryan on 4/21/15.
 */
public class RootNamespace extends MapNamespace {
    public static final String TRANS_METAS = "transMetas";

    private static final Map<String, Object> prepMap(Map<String, Object> map)  {
        Map<String, Object> result = new HashMap<>(map);
        if (!result.containsKey(TRANS_METAS)) {
            result.put(TRANS_METAS, new HashMap<>());
        }
        return result;
    }
    public RootNamespace(Map<String, Object> map) {
        super(null, null, prepMap(map), new DelegatingNamespaceFactory.Builder().addDelegate(TRANS_METAS, new TransMetasNamespace.Factory()).build());
    }
}

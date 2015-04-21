package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.BeanMethodMapping;
import com.github.brosander.kettle.vertx.namespace.factories.DelegatingNamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.BeanNamespace;
import org.pentaho.di.trans.TransMeta;

/**
 * Created by bryan on 4/21/15.
 */
public class TransMetaNamespace extends BeanNamespace {
    public static final String STEP_METAS = "stepMetas";
    public static final String GET_STEPS = "getSteps";

    public TransMetaNamespace(String prefix, String name, Object object) {
        super(prefix, name, object, new DelegatingNamespaceFactory.Builder().addDelegate(STEP_METAS, new StepMetasNamespace.Factory()).build(),
                new BeanMethodMapping.Builder(TransMeta.class).addSelfManagingProperty(STEP_METAS).addGetterMethod(STEP_METAS, GET_STEPS).build());
    }

    public static class Factory implements NamespaceFactory {

        @Override
        public Namespace create(String prefix, String name, Object object) {
            return new TransMetaNamespace(prefix, name, object);
        }
    }
}

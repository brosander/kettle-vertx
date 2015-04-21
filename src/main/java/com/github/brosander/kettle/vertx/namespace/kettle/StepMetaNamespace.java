package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.BeanMethodMapping;
import com.github.brosander.kettle.vertx.namespace.factories.DelegatingNamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.BeanNamespace;
import org.pentaho.di.trans.step.StepMeta;

/**
 * Created by bryan on 4/21/15.
 */
public class StepMetaNamespace extends BeanNamespace {
    public StepMetaNamespace(String prefix, String name, Object object) {
        super(prefix, name, object, new DelegatingNamespaceFactory.Builder().build(), new BeanMethodMapping.Builder(StepMeta.class).build());
    }

    public static class Factory implements NamespaceFactory {

        @Override
        public Namespace create(String prefix, String name, Object object) {
            return new StepMetaNamespace(prefix, name, object);
        }
    }
}

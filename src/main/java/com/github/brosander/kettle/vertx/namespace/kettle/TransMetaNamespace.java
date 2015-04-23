package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.ActionHandler;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.BeanMethodMapping;
import com.github.brosander.kettle.vertx.namespace.factories.DelegatingNamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.ActionBeanNamespace;
import com.github.brosander.kettle.vertx.namespace.generic.ActionHandlerMap;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/21/15.
 */
public class TransMetaNamespace extends ActionBeanNamespace {
    public static final String STEP_METAS = "stepMetas";
    public static final String GET_STEPS = "getSteps";
    public static final String SUCCESSFULLY_STARTED_TRANSFORMATION = "Successfully started transformation ";

    public TransMetaNamespace(String prefix, String name, final Object object) {
        super(prefix, name, object, new DelegatingNamespaceFactory.Builder().addDelegate(STEP_METAS, new StepMetasNamespace.Factory()).build(),
                new BeanMethodMapping.Builder(TransMeta.class).addSelfManagingProperty(STEP_METAS).addGetterMethod(STEP_METAS, GET_STEPS).build(), new ActionHandlerMap.Builder().addActionHandler("start", new ActionHandler() {
                    @Override
                    public boolean handle(Message<JsonObject> message) throws ActionException {
                        TransMeta transMeta = (TransMeta) object;
                        Trans trans = new Trans(transMeta);
                        try {
                            trans.execute(new String[]{});
                            message.reply(SUCCESSFULLY_STARTED_TRANSFORMATION + transMeta.getName());
                        } catch (KettleException e) {
                            throw new ActionException(500, "Error executing transformation: " + e.getMessage());
                        }
                        return true;
                    }
                }).build());
    }

    public static class Factory implements NamespaceFactory {

        @Override
        public Namespace create(String prefix, String name, Object object) {
            return new TransMetaNamespace(prefix, name, object);
        }
    }
}

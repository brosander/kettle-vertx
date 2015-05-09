package com.github.brosander.kettle.vertx.namespace.kettle.trans;

import com.github.brosander.kettle.vertx.jsonObject.BeanConverter;
import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.ActionHandler;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.BeanMethodMapping;
import com.github.brosander.kettle.vertx.namespace.factories.DelegatingNamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.ActionBeanNamespace;
import com.github.brosander.kettle.vertx.namespace.generic.ActionHandlerMap;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransListener;
import org.pentaho.di.trans.TransMeta;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bryan on 4/21/15.
 */
public class TransMetaNamespace extends ActionBeanNamespace {
    public static final String STEP_METAS = "stepMetas";
    public static final String GET_STEPS = "getSteps";
    public static final String SUCCESSFULLY_STARTED_TRANSFORMATION = "Successfully started transformation ";
    public static final String FINISHED = "finished";
    public static final String TYPE = "type";
    public static final String KETTLE_VERTICAL_TRANS_STATUS = "kettle-vertical-trans-status";
    public static final String RESULT = "result";
    public static final String STARTED = "started";
    public static final String TRANS_META = "transMeta";
    public static final String ID = "id";

    public TransMetaNamespace(final Vertx vertx, String prefix, String name, final Object object) {
        this(vertx, prefix, name, object, new ConcurrentHashMap<String, Trans>());
    }

    public TransMetaNamespace(final Vertx vertx, String prefix, String name, final Object object, final Map<String, Trans> executions) {
        super(vertx, prefix, name, object, new DelegatingNamespaceFactory.Builder().addDelegate(STEP_METAS, new StepMetasNamespace.Factory()).build(),
                new BeanMethodMapping.Builder(TransMeta.class).addSelfManagingProperty(STEP_METAS).addGetterMethod(STEP_METAS, GET_STEPS).build(), new ActionHandlerMap.Builder().addActionHandler("start", new ActionHandler() {
                    @Override
                    public boolean handle(Message<JsonObject> message) throws ActionException {
                        final TransMeta transMeta = (TransMeta) object;
                        final String transId = UUID.randomUUID().toString();
                        Trans trans = new Trans(transMeta);
                        executions.put(transId, trans);
                        trans.addTransListener(new TransListener() {
                            private final EventBus eventBus = vertx.eventBus();
                            private final BeanConverter<Result> resultBeanConverter = BeanConverter.forClass(Result.class, new HashSet<String>(Arrays.asList("getXML")));

                            @Override
                            public void transStarted(Trans trans) throws KettleException {
                                JsonObject message = new JsonObject();
                                message.putString(TYPE, STARTED);
                                message.putString(TRANS_META, transMeta.getName());
                                message.putString(ID, transId);
                                eventBus.publish(KETTLE_VERTICAL_TRANS_STATUS, message);
                            }

                            @Override
                            public void transActive(Trans trans) {

                            }

                            @Override
                            public void transFinished(Trans trans) throws KettleException {
                                JsonObject message = new JsonObject();
                                message.putString(TYPE, FINISHED);
                                message.putObject(RESULT, resultBeanConverter.convert(trans.getResult()));
                                message.putString(ID, transId);
                                message.putString(TRANS_META, transMeta.getName());
                                eventBus.publish(KETTLE_VERTICAL_TRANS_STATUS, message);
                            }
                        });
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
        public Namespace create(Vertx vertx, String prefix, String name, Object object) {
            return new TransMetaNamespace(vertx, prefix, name, object);
        }
    }
}

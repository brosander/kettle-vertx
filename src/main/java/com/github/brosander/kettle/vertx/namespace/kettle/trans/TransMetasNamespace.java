package com.github.brosander.kettle.vertx.namespace.kettle.trans;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.ActionHandler;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.ActionHandlerMap;
import com.github.brosander.kettle.vertx.namespace.generic.ActionMapNamespace;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.TransMeta;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * Created by bryan on 4/21/15.
 */
public class TransMetasNamespace extends ActionMapNamespace {
    public static final String MUST_SPECIFY_NAME_TO_OPERATE_ON = "Must specify name to operate on";
    public static final String SUCCESSFULLY_CREATED_TRANS_META = "Successfully created TransMeta ";
    public static final String SUCCESSFULLY_REMOVED_TRANS_META = "Successfully removed TransMeta ";
    public static final String TRANS_META_NOT_FOUND = "TransMeta not found: ";
    public static final String FILENAME = "filename";
    public static final String MUST_SPECIFY_FILENAME_TO_LOAD_FOR_FILENAME_TRANS_CREATE = "Must specify filename to load for filename trans create";
    public static final String ERROR_LOADING_TRANSFORMATION = "Error loading transformation ";
    public static final String TRANSFORMATION = "Transformation ";
    public static final String ALREADY_EXISTS = " already exists";

    public TransMetasNamespace(Vertx vertx, String prefix, String name, Map<String, TransMeta> transMetaMap) {
        super(vertx, prefix, name, transMetaMap, new TransMetaNamespace.Factory());
        setActionHandler(new ActionHandlerMap.Builder().addActionHandler("create", new ActionHandler() {

            @Override
            public boolean handle(Message<JsonObject> message) throws ActionException {
                String transName = getTransName(message);
                if (containsKey(transName)) {
                    throw new ActionException(500, TRANSFORMATION + transName + ALREADY_EXISTS);
                }
                TransMeta transMeta = new TransMeta();
                transMeta.setName(transName);
                put(transName, transMeta);
                message.reply(SUCCESSFULLY_CREATED_TRANS_META + transName);
                return true;
            }
        }).addActionHandler("loadFile", new ActionHandler() {

            @Override
            public boolean handle(Message<JsonObject> message) throws ActionException {
                String filename = message.body().getString(FILENAME);
                if (filename == null || filename.length() == 0) {
                    throw new ActionException(400, MUST_SPECIFY_FILENAME_TO_LOAD_FOR_FILENAME_TRANS_CREATE);
                }
                String transName = getTransName(message);
                if (containsKey(transName)) {
                    throw new ActionException(500, TRANSFORMATION + transName + ALREADY_EXISTS);
                }
                try {
                    TransMeta value = new TransMeta(filename);
                    value.setName(transName);
                    put(transName, value);
                    message.reply(SUCCESSFULLY_CREATED_TRANS_META + transName);
                    return true;
                } catch (KettleException e) {
                    throw new ActionException(500, ERROR_LOADING_TRANSFORMATION + e.getMessage());
                }
            }
        }).addActionHandler("delete", new ActionHandler() {

            @Override
            public boolean handle(Message<JsonObject> message) throws ActionException {
                String transName = getTransName(message);
                if (!remove(transName)) {
                    throw new ActionException(404, TRANS_META_NOT_FOUND + transName);
                } else {
                    message.reply(SUCCESSFULLY_REMOVED_TRANS_META + transName);
                }
                return true;
            }
        }).build());
    }

    public static String getTransName(Message<JsonObject> message) throws ActionException {
        String name = message.body().getString("name");
        if (name == null || name.length() == 0) {
            throw new ActionException(400, MUST_SPECIFY_NAME_TO_OPERATE_ON);
        }
        return name;
    }

    public static final class Factory implements NamespaceFactory {

        @Override
        public Namespace create(Vertx vertx, String prefix, String name, Object object) {
            return new TransMetasNamespace(vertx, prefix, name, (Map<String, TransMeta>) object);
        }
    }
}
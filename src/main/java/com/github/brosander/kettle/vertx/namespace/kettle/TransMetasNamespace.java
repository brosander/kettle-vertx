package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.ActionException;
import com.github.brosander.kettle.vertx.namespace.ActionHandler;
import com.github.brosander.kettle.vertx.namespace.Namespace;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.ActionHandlerMap;
import com.github.brosander.kettle.vertx.namespace.generic.ActionMapNamespace;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.TransMeta;
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

    public TransMetasNamespace(String prefix, String name, final Map<String, TransMeta> transMetaMap) {
        super(prefix, name, transMetaMap, new TransMetaNamespace.Factory(), new ActionHandlerMap.Builder().addActionHandler("create", new ActionHandler() {

            @Override
            public boolean handle(Message<JsonObject> message) throws ActionException {
                TransMeta transMeta = new TransMeta();
                String transName = getTransName(message);
                transMeta.setName(transName);
                transMetaMap.put(transName, transMeta);
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
                try {
                    TransMeta value = new TransMeta(filename);
                    String transName = getTransName(message);
                    value.setName(transName);
                    transMetaMap.put(transName, value);
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
                TransMeta remove = transMetaMap.remove(transName);
                if (remove == null) {
                    throw new ActionException(404, TRANS_META_NOT_FOUND + transName);
                } else {
                    message.reply(SUCCESSFULLY_REMOVED_TRANS_META + transName);
                }
                return true;
            }
        }).build());
    }

    private static String getTransName(Message<JsonObject> message) throws ActionException {
        String name = message.body().getString("name");
        if (name == null || name.length() == 0) {
            throw new ActionException(400, MUST_SPECIFY_NAME_TO_OPERATE_ON);
        }
        return name;
    }

    public static final class Factory implements NamespaceFactory {

        @Override
        public Namespace create(String prefix, String name, Object object) {
            return new TransMetasNamespace(prefix, name, (Map<String, TransMeta>) object);
        }
    }
}
package com.github.brosander.kettle.vertx.namespace.kettle;

import com.github.brosander.kettle.vertx.namespace.Action;
import com.github.brosander.kettle.vertx.namespace.factories.NamespaceFactory;
import com.github.brosander.kettle.vertx.namespace.generic.ActionMapNamespace;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.TransMeta;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * Created by bryan on 4/19/15.
 */
public class TransMetasActionMapNamespace extends ActionMapNamespace {
    public static final String SUCCESSFULLY_CREATED_TRANS_META = "Successfully created TransMeta ";
    public static final String SUCCESSFULLY_REMOVED_TRANS_META = "Successfully removed TransMeta ";
    public static final String TRANS_META_NOT_FOUND = "TransMeta not found: ";
    public static final String FILENAME = "filename";
    public static final String MUST_SPECIFY_FILENAME_TO_LOAD_FOR_FILENAME_TRANS_CREATE = "Must specify filename to load for filename trans create";
    public static final String ERROR_LOADING_TRANSFORMATION = "Error loading transformation ";
    private final Map<String, TransMeta> transMetaMap;

    public TransMetasActionMapNamespace(String prefix, String name, Map<String, TransMeta> transMetaMap, NamespaceFactory namespaceFactory) {
        super(prefix, name, transMetaMap, namespaceFactory);
        this.transMetaMap = transMetaMap;
    }

    @Action
    public void loadFile(String transName, Message<JsonObject> message) {
        String filename = message.body().getString(FILENAME);
        if (filename == null || filename.length() == 0) {
            message.fail(400, MUST_SPECIFY_FILENAME_TO_LOAD_FOR_FILENAME_TRANS_CREATE);
            return;
        }
        try {
            TransMeta value = new TransMeta(filename);
            value.setName(transName);
            transMetaMap.put(transName, value);
            message.reply(SUCCESSFULLY_CREATED_TRANS_META + transName);
            return;
        } catch (KettleException e) {
            message.fail(500, ERROR_LOADING_TRANSFORMATION + e.getMessage());
            return;
        }
    }

    @Action
    public void create(String transName, Message<JsonObject> message) {
        TransMeta transMeta = new TransMeta();
        transMeta.setName(transName);
        transMetaMap.put(transName, transMeta);
        message.reply(SUCCESSFULLY_CREATED_TRANS_META + transName);
    }

    @Action
    public void delete(String transName, Message<JsonObject> message) {
        TransMeta remove = transMetaMap.remove(transName);
        if (remove == null) {
            message.fail(404, TRANS_META_NOT_FOUND + transName);
        } else {
            message.reply(SUCCESSFULLY_REMOVED_TRANS_META + transName);
        }
    }
}

package com.github.brosander.kettle.vertx.repository;

import org.pentaho.di.repository.ObjectId;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by bryan on 4/19/15.
 */
public class JsonObjectId implements ObjectId {
    private final String id;

    public JsonObjectId(JsonObject jsonObject) {
        this.id = jsonObject.encodePrettily();
    }

    @Override
    public String getId() {
        return id;
    }
}

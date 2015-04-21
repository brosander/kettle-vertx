package com.github.brosander.kettle.vertx.namespace;

/**
 * Created by bryan on 4/21/15.
 */
public class ActionException extends Exception {
    private final int code;
    private final String message;

    public ActionException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {

        return code;
    }
}

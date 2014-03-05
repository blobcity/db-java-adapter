/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.service.requests;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abstraction for a request to be sent to the server. The subclasses of this class will create a
 * request JSON to be sent to the server.
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public abstract class Request {

    protected String appId;
    protected String appKey;
    public static final String APP_PARAM = "app";
    public static final String KEY_PARAM = "key";
    public static final String Q_PARAM = "q";

    public abstract CommandName commandName();

    protected JSONObject createWithAppCredentials(final String appId, final String appKey) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(APP_PARAM, appId);
        json.put(KEY_PARAM, appKey);
        json.put(Q_PARAM, commandName().getQueryName());
        return json;
    }

    public Request(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }
}

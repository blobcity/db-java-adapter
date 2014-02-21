/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.service.requests;

import com.blobcity.util.ServiceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class ListOperationsRequest {

    private static final Logger logger = Logger.getLogger(ListOperationsRequest.class.getName());
    private String q = CommandName.LIST_OPS.getQueryName();
    private String appId;
    private String tableName;

    public ListOperationsRequest app(String appId) {
        this.appId = appId;
        return this;
    }

    public ListOperationsRequest t(String t) {
        this.tableName = t;
        return this;
    }

    public JSONObject createRequest() {
        try {
            JSONObject json = new JSONObject();
            json.put("app", appId);
            json.put("t", tableName);
            json.put("q", q);
            return json;
        } catch (JSONException e) {
            String msg = "Failed to create JSON for LIST-OPS. AppId=" + appId + ", tableName=" + tableName;
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceException(msg, e);
        }
    }
}

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
 * Implementation of Fetch Schema request.
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class FetchSchemaRequest extends Request {

    private static final Logger logger = Logger.getLogger(FetchSchemaRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.LIST_SCHEMA;
    }

    public FetchSchemaRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest(final String tableName) {
        try {
            JSONObject json = createWithAppCredentials(appId, appKey);
            json.put("t", tableName);
            return json;
        } catch (JSONException ex) {
            String msg = "Failed to create json for fetchSchema. AppId=" + appId;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceException(msg, ex);
        }
    }
}

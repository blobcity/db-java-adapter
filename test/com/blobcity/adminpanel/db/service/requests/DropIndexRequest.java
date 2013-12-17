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
public class DropIndexRequest extends Request {

    private static final Logger logger = Logger.getLogger(CreateIndexRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.DROP_INDEX;
    }

    public DropIndexRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest(final String tableName, final String columnName) {
        try {
            JSONObject json = createWithAppCredentials(appId, appKey);
            json.put("t", tableName);
            JSONObject payload = new JSONObject();
            payload.put("name", columnName);
            json.put("p", payload);
            return json;
        } catch (JSONException e) {
            String msg = "Failed to create JSON for drop-index. AppId=" + appId + ", tableName=" + tableName + ", col=" + columnName;
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceException(msg, e);
        }
    }
}

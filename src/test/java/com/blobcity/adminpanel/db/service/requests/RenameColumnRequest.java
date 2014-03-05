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
 * Implementation of rename-columns request
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class RenameColumnRequest extends Request {

    private static final Logger logger = Logger.getLogger(RenameColumnRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.RENAME_COLUMN;
    }

    public RenameColumnRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest(final String tableName, final String oldColumnName, final String newColumnName) {
        try {
            JSONObject json = createWithAppCredentials(appId, appKey);
            json.put("t", tableName);
            JSONObject payload = new JSONObject();
            payload.put("name", oldColumnName);
            payload.put("new-name", newColumnName);
            json.put("p", payload);
            return json;
        } catch (JSONException ex) {
            String msg = "JSON creation failed in renameColumn. AppId=" + appId + ", tableName=" + tableName + ", column=" + oldColumnName + ", new-name=" + newColumnName;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceException(msg, ex);
        }
    }
}

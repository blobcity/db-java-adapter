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
 * Implementation of Drop Column Request
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class DropColumnRequest extends Request {

    private static final Logger logger = Logger.getLogger(DropColumnRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.DROP_COLUMN;
    }

    public DropColumnRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest(final String tableName, final String columnName) {
        try {
            JSONObject json = createWithAppCredentials(appId, appKey);
            json.put("t", tableName);
            JSONObject p = new JSONObject();
            p.put("name", columnName);
            json.put("p", p);
            return json;
        } catch (JSONException ex) {
            String msg = "JSON creation failed for deleteColumn. appId=" + appId + ", tableName=" + tableName + ", columnName=" + columnName;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceException(ex);
        }
    }
}

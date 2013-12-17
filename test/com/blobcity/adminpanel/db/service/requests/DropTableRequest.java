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
 * Implementation of Drop Table request. 
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class DropTableRequest extends Request {

    private static final Logger logger = Logger.getLogger(DropTableRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.DROP_TABLE;
    }

    public DropTableRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest(final String tableName) {
        try {
            JSONObject json = createWithAppCredentials(appId, appKey);
            json.put("t", tableName);
            return json;
        } catch (JSONException ex) {
            String msg = "Failed to create json for dropTable. AppId=" + appId;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceException(msg, ex);
        }
    }
}

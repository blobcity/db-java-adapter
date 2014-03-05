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
 * Implementation of List-Tables request
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class ListTablesRequest extends Request {

    private static final Logger logger = Logger.getLogger(ListTablesRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.LIST_TABLES;
    }

    public ListTablesRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest() {
        try {
            JSONObject json = createWithAppCredentials(appId, appKey);
            return json;
        } catch (JSONException ex) {
            String msg = "Failed to create json for fetchTables. AppId=" + appId;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceException(msg, ex);
        }
    }
}

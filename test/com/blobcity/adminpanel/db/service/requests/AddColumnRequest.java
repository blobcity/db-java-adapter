/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.service.requests;

import com.blobcity.adminpanel.db.bo.Column;
import com.blobcity.util.ServiceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of Add-Column request. 
 * 
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class AddColumnRequest extends Request {

    private static final Logger logger = Logger.getLogger(AddColumnRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.ADD_COLUMN;
    }

    public AddColumnRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest(final String tableName, final Column column) {
        try {
            JSONObject json = createWithAppCredentials(appId, appKey);
            json.put("t", tableName);
            json.put("p", toJSON(column));
            return json;
        } catch (JSONException ex) {
            String msg = "JSON creation failed for addColumn. Appid=" + appId + ", table=" + tableName + ", column=" + column.toString();
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceException(msg, ex);
        }
    }

    private JSONObject toJSON(Column c) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("name", c.getName());
        object.put("type", c.getType().getJSONName());
        object.put("auto-define", c.getAutoDefineType().toString());
        object.put("index", c.getIndex().toString());
        return object;
    }
}

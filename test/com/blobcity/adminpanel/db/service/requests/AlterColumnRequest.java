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
 * Implementation of Alter-Column request. 
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class AlterColumnRequest extends Request {

    private static final Logger logger = Logger.getLogger(AlterColumnRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.ALTER_COLUMN;
    }

    public AlterColumnRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest(final String tableName, final Column column) {
        try {
            JSONObject json = createWithAppCredentials(appId, appKey);
            json.put("t", tableName);
            json.put("p", toJSON(column));
            return json;
        } catch (JSONException ex) {
            String msg = "JSON creation failed in alterColumn. AppId=" + appId + ", tableName=" + tableName + ", column=" + column.toString();
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceException(msg, ex);
        }
    }

    private JSONObject toJSON(Column c) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("name", c.getName());
        object.put("type", c.getType().getJSONName());
        object.put("auto-define", c.getAutoDefineType().toString());
        return object;
    }
}

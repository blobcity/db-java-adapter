/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.service.requests;

import com.blobcity.adminpanel.db.bo.Column;
import com.blobcity.adminpanel.db.bo.Table;
import com.blobcity.util.ServiceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of Create Table request. 
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class CreateTableRequest extends Request {

    private static final Logger logger = Logger.getLogger(CreateTableRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.CREATE_TABLE;
    }

    public CreateTableRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest(final Table table) {
        try {
            JSONObject jsonObject = createWithAppCredentials(appId, appKey);
            jsonObject.put("t", table.getName());
            if (!table.hasColumns()) {
                return jsonObject;
            }
            JSONObject p = new JSONObject();
            Column primaryColumn = table.getPrimaryColumn();
            p.put("primary", primaryColumn.getName());
            p.put(primaryColumn.getName(), toJSON(primaryColumn));
            for (Column c : table.getColumns()) {
                if (!c.isPrimaryKey()) {
                    p.put(c.getName(), toJSON(c));
                }
            }
            jsonObject.put("p", p);
            return jsonObject;
        } catch (JSONException ex) {
            String msg = "json creation failed in createTable. AppId=" + appId + ", table=" + table.toString();
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceException(msg, ex);
        }
    }

    private JSONObject toJSON(final Column c) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("type", c.getType().getJSONName());
        object.put("auto-define", c.getAutoDefineType().toString());
        object.put("index", c.getIndex().toString());
        return object;
    }
}

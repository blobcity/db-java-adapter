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
 * Implementation of Rename Table request
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class RenameTableRequest extends Request {

    private static final Logger logger = Logger.getLogger(RenameTableRequest.class.getName());

    @Override
    public CommandName commandName() {
        return CommandName.RENAME_TABLE;
    }

    public RenameTableRequest(String appId, String appKey) {
        super(appId, appKey);
    }

    public JSONObject createRequest(final String oldTableName, final String newTableName) {
        try {
            JSONObject json = createWithAppCredentials(appId, appKey);
            json.put("t", oldTableName);
            JSONObject p = new JSONObject();
            p.put("new-name", newTableName);
            json.put("p", p);
            return json;
        } catch (JSONException ex) {
            String msg = "JSON creation failed in renameTable. AppId=" + appId + ", oldTableName=" + oldTableName + ", newTableName=" + newTableName;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceException(msg, ex);
        }
    }
}

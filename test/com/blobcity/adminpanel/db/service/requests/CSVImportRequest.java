/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.service.requests;

import com.blobcity.util.ServiceException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class CSVImportRequest {

    private static final Logger logger = Logger.getLogger(CSVImportRequest.class.getName());
    private String q = CommandName.BULK_IMPORT.getQueryName();
    private String appId;
    private String tableName;
    private String file;
    private Map<String, String> columnMapping;

    public CSVImportRequest app(String appId) {
        this.appId = appId;
        return this;
    }

    public CSVImportRequest t(String t) {
        this.tableName = t;
        return this;
    }

    public CSVImportRequest file(String file) {
        this.file = file;
        return this;
    }

    public CSVImportRequest columnMapping(Map<String, String> colMapping) {
        this.columnMapping = colMapping;
        return this;
    }

    public JSONObject createRequest() {
        try {
            JSONObject json = new JSONObject();
            json.put("app", appId);
            json.put("t", tableName);
            json.put("q", q);
            JSONObject payload = new JSONObject();
            payload.put("file", file);
            payload.put("type", "CSV");
            if (columnMapping != null && !columnMapping.isEmpty()) {
                payload.put("columnMapping", generateColumnMapping(columnMapping));
            }
            json.put("p", payload);
            return json;
        } catch (JSONException e) {
            String msg = "Failed to create JSON for CSVImportRequest. AppId=" + appId + ", tableName=" + tableName + ", file=" + file;
            logger.log(Level.SEVERE, msg, e);
            throw new ServiceException(msg, e);
        }
    }

    private JSONObject generateColumnMapping(Map<String, String> colMapping) throws JSONException {
        JSONObject colMapJSON = new JSONObject();
        for (Map.Entry<String, String> entry : colMapping.entrySet()) {
            colMapJSON.put(entry.getKey(), entry.getValue());
        }
        return colMapJSON;
    }
}

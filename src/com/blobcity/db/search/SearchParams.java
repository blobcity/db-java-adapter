/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sanket Sarang
 */
public class SearchParams {

    private Map<String, Set<Object>> map = new HashMap<String, Set<Object>>();

    public void add(String columnName, Object value) {
        if (value == null) {
            return;
        }
        
        if (!map.containsKey(columnName)) {
            map.put(columnName, new HashSet<Object>());
        }

        map.get(columnName).add(value);
    }

    public JSONObject asJson() {
        JSONObject jsonObject = new JSONObject();
        for(String key : map.keySet()) {
            Set<Object> valueSet = map.get(key);
            try {
                jsonObject.put(key, valueSet);
            } catch (JSONException ex) {
                Logger.getLogger(SearchParams.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonObject;
    }
}

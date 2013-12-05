/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
        return new JSONObject(map);
    }
}

package com.blobcity.adminpanel.db.bo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a database table
 *
 * @author akshay
 */
public class Table {

    /**
     * Name of the table, unique for the particular application.
     */
    private String name;
    /**
     * The parent Application ID of the table
     */
    private String appId;
    /**
     * The columns contained in this table
     */
    private List<Column> columns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * Returns the column marked as PrimaryKey. It will return the first column that it finds
     *
     * @return A column object that is marked as the primary key
     */
    public Column getPrimaryColumn() {
        for (Column c : columns) {
            if (c.isPrimaryKey()) {
                return c;
            }
        }
        return null;
    }

    public void addColumn(Column c) {
        if (this.columns == null) {
            this.columns = new ArrayList<Column>();
        }
        columns.add(c);
    }

    public void addColumnsFromPayload(JSONObject payload) throws JSONException {
        Iterator keys = payload.keys();
        String primaryColumnName = payload.getString("primary");
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (!key.equals("primary")) {
                Column col = Column.fromJSON(payload.getJSONObject(key));
                col.setName(key);
                col.setPrimaryKey(primaryColumnName.equals(key));
                this.addColumn(col);
            }
        }
    }

    public boolean hasColumns() {
        return columns != null && !columns.isEmpty();
    }
}

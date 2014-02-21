package com.blobcity.adminpanel.db.bo;

import com.blobcity.util.Preconditions;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a column of a DB table.
 *
 * @author akshay
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public class Column {

    /**
     * The name of the column, unique for a table.
     */
    private String name;
    /**
     * The type of the column
     */
    private ColumnType type;
    /**
     * Is this column the primary key?
     */
    private boolean primaryKey;
    /**
     * Both primary and non-primary columns can be auto-defined. auto-defined can be of various types. Default type is
     * NONE for non auto-defined columns.
     */
    private AutoDefineType autoDefineType;
    /**
     * The type of the index if this column is index. Type is NONE if the column is not indexed.
     */
    private IndexType index;
    
    public Column() {
        this.autoDefineType = AutoDefineType.NONE;
        this.index = IndexType.NONE;
    }

    public Column(Column that) {
        this.name = that.name;
        this.type = that.type;
        this.primaryKey = that.primaryKey;
        this.autoDefineType = that.autoDefineType;
        this.index = that.index;
    }

    public static Column fromJSON(JSONObject jsonObject) throws JSONException {
        Column c = new Column();
        c.type = ColumnType.fromJSONName(jsonObject.getString("type"));
        c.autoDefineType = AutoDefineType.fromJSONName(jsonObject.getString("auto-define"));
        c.index = IndexType.fromJSONName(jsonObject.getString("index"));
        return c;
    }
    
    /**
     * A readable way to compare column type
     * @param colType The column type to compare
     * @return <code>true</code> if the argument matches the column type of this object, otherwise <code>false</code>
     */
    public boolean isOfType(ColumnType colType) {
        Preconditions.checkNotNull(colType, "colType must not be null");
        if(type == null) {
            return false;
        }
        return type.equals(colType);
    }
    
    /**
     * Sets the primary key flag to true and makes the index UNIQUE
     */
    public void makePrimaryKey() {
        this.primaryKey = true;
        this.index = IndexType.UNIQUE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public AutoDefineType getAutoDefineType() {
        return autoDefineType;
    }

    public void setAutoDefineType(AutoDefineType autoDefineType) {
        this.autoDefineType = autoDefineType;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public IndexType getIndex() {
        return index;
    }

    public void setIndex(IndexType index) {
        this.index = index;
    }
}

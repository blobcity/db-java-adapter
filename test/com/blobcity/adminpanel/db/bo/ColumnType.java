
package com.blobcity.adminpanel.db.bo;

/**
 *
 * @author akshay
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public enum ColumnType {
    
    INTEGER("int", true),
    FLOAT("float", true),
    LONG("long", true),
    DOUBLE("double", true),
    STRING("string", true),
    CHARACTER("char", true),
    LIST_INTEGER("list<int>"),
    LIST_FLOAT("list<float>"),
    LIST_LONG("list<long>"),
    LIST_DOUBLE("list<double>"),
    LIST_STRING("list<string>"),
    LIST_CHARACTER("list<char>");
    
    private boolean primaryKeyAllowed;    
    private String jsonName;
    
    ColumnType(String jsonName, boolean pkAllowed) {
        this.jsonName = jsonName;
        this.primaryKeyAllowed = pkAllowed;
    }
    
    ColumnType(String jsonName) {
        this.jsonName = jsonName;
        this.primaryKeyAllowed = false;
    }
    
    public static ColumnType fromJSONName(String type) {
        for(ColumnType colType : values()) {
            if(colType.getJSONName().equals(type)) {
                return colType;
            }
        }
        return null;
    }
    
    public boolean isPrimaryKeyAllowed() {
        return this.primaryKeyAllowed;
    }
    
    public String getJSONName() {
        return this.jsonName;
    }
    
    public boolean isIndexAllowed() {
        //Currently indexAllowed == primaryKeyAllowed. Add this property later on if needed.
        return this.primaryKeyAllowed;
    }
}

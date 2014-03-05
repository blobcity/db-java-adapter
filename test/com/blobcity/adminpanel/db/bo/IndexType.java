
package com.blobcity.adminpanel.db.bo;

/**
 * The type of index.
 * @author akshay
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public enum IndexType {

    NONE,
    UNIQUE,
    BTREE,
    BITMAP;
    
    public static IndexType fromJSONName(String jsonName) {
        if(jsonName == null) {
            return null;
        }
        for(IndexType type : values()) {
            if(type.toString().equals(jsonName.toUpperCase())) {
                return type;
            }
        }
        return null;
    }
    
    public static IndexType[] enabledIndexes() {
        return new IndexType[] { NONE, UNIQUE, BTREE };
    }
    
}

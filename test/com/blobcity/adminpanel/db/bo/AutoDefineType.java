package com.blobcity.adminpanel.db.bo;

/**
 * The type of any auto defined column. None if the column is not auto defined.
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public enum AutoDefineType {

    /**
     * Default type for non auto-defined columns
     */
    NONE,
    /**
     * Ensures uniqueness. Can only be applied to columns of VARCHAR data type
     */
    UUID,
    /**
     * Serially incrementing decimal values. Can be applied only to columns with
     * INT and LONG data types
     */
    /**
    SERIAL,
     * Killed the Serial type
     */
    
    /**
     * System timestamp of database server. Can be applied only to columns with
     * LONG
     */
    TIMESTAMP;
    
    public static AutoDefineType fromJSONName(String jsonValue) {
        if(jsonValue == null) {
            return null;
        }
        for(AutoDefineType type : values()) {
            if(type.toString().equals(jsonValue.toUpperCase())) {
                return type;
            }
        }
        return null;
    }
    
}

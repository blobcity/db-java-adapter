/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.enums;

/**
 *
 * @author Prikshit Kumar <prikshit.kumar@blobcity.com>
 */
public enum TableType {
    
    ON_DISK("on-disk"),
    IN_MEMORY("in-memory"),
    IN_MEMORY_NON_DURABLE("in-memory-nd");
    private final String type;

    TableType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

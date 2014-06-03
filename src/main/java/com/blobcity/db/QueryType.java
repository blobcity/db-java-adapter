/*
 * Copyright 2011 - 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

/**
 *
 * @author Karishma
 * @author Sanket Sarang
 */
enum QueryType {

    LOAD("select"),
    SAVE("save"),
    INSERT("insert"), 
    REMOVE("delete"),
    SELECT_ALL("select-all"),
    SEARCH("search"),
    CONTAINS("contains"),
    STORED_PROC("sp");
    
    private final String queryCode;
    
    QueryType(final String queryCode) {
        this.queryCode = queryCode;
    }

    public String getQueryCode() {
        return queryCode;
    }
}

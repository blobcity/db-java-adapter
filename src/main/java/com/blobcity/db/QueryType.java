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
    
    // data related commands
    LOAD("select"),
    SAVE("save"),
    INSERT("insert"), 
    REMOVE("delete"),
    SELECT_ALL("select-all"),
    SEARCH("search"),
    CONTAINS("contains"),
    // database related commands
    CREATE_TABLE("create-table"),
    DROP_TABLE("drop-table"),
    TRUNCATE_TABLE("truncate-table"),
    
    ADD_COLUMN("add-column"),
    DROP_COLUMN("drop-column"),
    
    INDEX("index"),
    DROP_INDEX("drop-index"),
    // user-provided code related commands
    SEARCH_FILTERED("search-filtered"),
    STORED_PROC("sp");
    
    private final String queryCode;
    
    QueryType(final String queryCode) {
        this.queryCode = queryCode;
    }

    public String getQueryCode() {
        return queryCode;
    }
}

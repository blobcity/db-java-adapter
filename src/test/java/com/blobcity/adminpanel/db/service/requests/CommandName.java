/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.service.requests;

/**
 * The list of DB Admin commands that the server supports
 * 
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public enum CommandName {

    LIST_TABLES("LIST-TABLES"),
    LIST_SCHEMA("LIST-SCHEMA"),
    DROP_TABLE("DROP-TABLE"),
    CREATE_TABLE("CREATE-TABLE"),
    RENAME_TABLE("RENAME-TABLE"),
    DROP_COLUMN("DROP-COLUMN"),
    ADD_COLUMN("ADD-COLUMN"),
    ALTER_COLUMN("ALTER-COLUMN"),
    RENAME_COLUMN("RENAME-COLUMN"),
    CREATE_INDEX("INDEX"),
    DROP_INDEX("DROP-INDEX"),
    BULK_IMPORT("BULK-IMPORT"),
    LIST_OPS("LIST-OPS");

    CommandName(String queryName) {
        this.queryName = queryName;
    }
    private String queryName;

    public String getQueryName() {
        return queryName;
    }
}

/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import java.text.MessageFormat;

/**
 * Internal class to represent a query to be sent to the database
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
class DbQueryRequest {

    private final String username;
    private final String password;
    private final String db;
    private final String query;

    private DbQueryRequest(final String username, final String password, final String db, final String query) {
        this.username = username;
        this.password = password;
        this.db = db;
        this.query = query;
    }

    public static DbQueryRequest create(final String username, final String password, final String db, final String query) {
        return new DbQueryRequest(username, password, db, query);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDb() {
        return db;
    }

    public String getQuery() {
        return query;
    }

    public String createPostParam() {
        return MessageFormat.format("username={0}&password={1}&db={2}&q={3}", username, password, db, query);
    }
}

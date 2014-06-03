/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.config.Credentials;
import java.text.MessageFormat;

/**
 * Internal class to represent a query to be sent to the database
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
class DbQueryRequest {

    private final Credentials credentials;
    private final String query;

    private DbQueryRequest(final Credentials credentials, final String query) {
        this.credentials = credentials;
        this.query = query;
    }

    public static DbQueryRequest create(final Credentials credentials, final String query) {
        return new DbQueryRequest(credentials, query);
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public String getQuery() {
        return query;
    }

    public String createPostParam() {
        return MessageFormat.format("username={0}&password={1}&db={2}&q={3}", credentials.getUsername(), credentials.getPassword(), credentials.getDb(), query);
    }
}

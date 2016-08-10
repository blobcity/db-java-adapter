/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.config.Credentials;
import com.blobcity.db.exceptions.DbOperationException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Internal class to represent a query to be sent to the database
 *
 * @author Karun AB
 * @author Sanket Sarang
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
        try {
            return MessageFormat.format("username={0}&password={1}&db={2}&q={3}", credentials.getUsername(), credentials.getPassword(), credentials.getDb(), URLEncoder.encode(query, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DbQueryRequest.class.getName()).log(Level.SEVERE, null, ex);
            throw new DbOperationException("UNKNOWN", "The requested data could not be encoded to UTF-8 format for transmission over network");
        }
    }
}

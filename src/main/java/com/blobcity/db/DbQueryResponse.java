/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.exceptions.DbOperationException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.util.List;

/**
 * Class to wrap responses from the database for internal representation before it gets returned to the client
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
class DbQueryResponse {

    // Ack code
    private final int ackCode;
    // Select keys
    private final List keys;
    // Contains
    private final boolean contains;
    // Error handling
    private final String errorCode;
    private final String errorCause;
    // Response data
    private final JsonElement payload;

    public DbQueryResponse(final String response) {
        final JsonObject jsonObj = new JsonParser().parse(response).getAsJsonObject();

        ackCode = jsonObj.get(QueryConstants.ACK).getAsInt();
        keys = new Gson().fromJson(jsonObj.get(QueryConstants.KEYS), new TypeToken<List>() {
        }.getType());

        final JsonElement containsElement = jsonObj.get(QueryConstants.CONTAINS);
        contains = containsElement != null ? jsonObj.get(QueryConstants.CONTAINS).getAsBoolean() : false;

        final JsonElement codeElement = jsonObj.get(QueryConstants.CODE);
        errorCode = codeElement != null ? jsonObj.get(QueryConstants.CODE).getAsString() : null;
        final JsonElement causeElement = jsonObj.get(QueryConstants.CAUSE);
        errorCause = causeElement != null ? jsonObj.get(QueryConstants.CAUSE).getAsString() : null;

        payload = jsonObj.get(QueryConstants.PAYLOAD);
    }

    public int getAckCode() {
        return ackCode;
    }

    public boolean isSuccessful() {
        return ackCode == 1;
    }

    public List getKeys() {
        return keys;
    }

    public boolean contains() {
        return contains;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public DbOperationException createException() {
        return new DbOperationException(errorCode, errorCause);
    }

    public JsonElement getPayload() {
        return payload;
    }
}

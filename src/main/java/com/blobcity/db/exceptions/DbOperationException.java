/*
 * Copyright 2011 - 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.exceptions;

/**
 * Represents any exception that occurs on the database along with the appropriate error code for the exception. More details on possible error codes can be
 * found at http://docs.blobcity.com/display/DB/Error+Codes
 *
 * @author Sanket Sarang
 */
public class DbOperationException extends RuntimeException {

    private static final long serialVersionUID = -7783116773042144796L;

    private String errorCode = "";

    public DbOperationException(final String errorCode) {
        this.errorCode = errorCode;
    }

    public DbOperationException(final String errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        final String s = getClass().getName();
        final String message = getLocalizedMessage();
        final String baseStr = (message != null) ? (s + ": " + message) : s;
        return errorCode != null && !"".equals(errorCode) ? baseStr + " (" + errorCode + ")" : baseStr;
    }
}

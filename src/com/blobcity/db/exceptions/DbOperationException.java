/*
 * Copyright 2011 - 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.exceptions;

/**
 * Represents any exception that occurs on the database along with the appropriate error code for the exception. More
 * details on possible error codes can be found at http://docs.blobcity.com/display/DB/Error+Codes
 *
 * @author Sanket Sarang
 */
public class DbOperationException extends RuntimeException {

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
        return "DbOperationException{" + "errorCode=" + errorCode + '}';
    }
}

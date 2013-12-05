/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.exceptions;

/**
 *
 * @author Sanket Sarang
 */
public class InternalAdapterException extends RuntimeException {

    public InternalAdapterException() {
        super("This error occurred interally within the adapter. Please make sure that your code and execution environment"
                + " is configured correctly. Contact BlobCity Support for further assistance.");
    }

    public InternalAdapterException(String message) {
        super(message);
    }

    public InternalAdapterException(Throwable throwable) {
        super(throwable);
    }

    public InternalAdapterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

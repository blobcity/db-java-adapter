/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */

package com.blobcity.db.exceptions;

/**
 * This exception is thrown when an error occurs that is internal to the database or adapter operation. The developer
 * would usually have no control for fixing this exception. Should this exception every arise you must contact BlobCity
 * administrators using the standard support contact points. 
 * 
 * It is reasonable to assume that such an exception will never arise and if at all it arises it is a major fault that
 * may need fixing from BlobCity side before your program becomes functional again.
 * 
 * If you encounter this exception after a recent update by BlobCity to it's API or database, you much check if a newer
 * version of the adapter is available and consider updating your adapter.
 * 
 * @author Sanket Sarang
 */
public class InternalException extends RuntimeException {
    
    public InternalException() {
        super("unknown error");
    }
    
    public InternalException(String message) {
        super(message);
    }
    
    public InternalException(Throwable throwable) {
        super(throwable);
    }
    
    public InternalException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

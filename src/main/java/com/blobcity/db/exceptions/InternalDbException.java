/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.exceptions;

/**
 * This exception is thrown when an error occurs that is internal to the database. The developer would usually have no control for fixing this exception. Should
 * this exception ever arise you must contact BlobCity administrators using the standard support contact points.
 * <p>
 * It is reasonable to assume that such an exception will never arise and if at all it arises it is a major fault that may need fixing from BlobCity side before
 * your program becomes functional again.
 * <p>
 * If you encounter this exception after a recent update by BlobCity to it's API or database, you must check if a newer version of the adapter is available and
 * consider updating your adapter.
 *
 * @author Sanket Sarang
 */
public class InternalDbException extends RuntimeException {

  private static final long serialVersionUID = -1096307649040743688L;

  public InternalDbException() {
    super("Unknown error. Please check if you are using the latest version of the adpater or contact BlobCity Support");
  }

  public InternalDbException(String message) {
    super(message);
  }

  public InternalDbException(Throwable throwable) {
    super(throwable);
  }

  public InternalDbException(String message, Throwable throwable) {
    super(message, throwable);
  }
}

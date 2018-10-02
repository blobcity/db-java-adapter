/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.exceptions;

/**
 * @author Sanket Sarang
 */
public class InternalAdapterException extends RuntimeException {

  private static final long serialVersionUID = -424291815649214740L;

  public InternalAdapterException() {
    super("This error occurred internally within the adapter. Please make sure that your code and execution environment"
      + " are configured correctly. Contact BlobCity Support for further assistance.");
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

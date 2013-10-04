/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.exceptions;

/**
 *
 * @author sanketsarang
 */
public class InvalidFieldException extends Exception {

    public InvalidFieldException(String message) {
        super(message);
    }

    public InvalidFieldException(Throwable cause) {
        super(cause);
    }

    public InvalidFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}

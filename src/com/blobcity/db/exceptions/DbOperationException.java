/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blobcity.db.exceptions;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public class DbOperationException extends Exception {
    
    private ExceptionType exceptionType;
    private String message;
    
    public DbOperationException(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
        message = "";
    }
    
    public DbOperationException(ExceptionType exceptionType, String message) {
        this.exceptionType = exceptionType;
        this.message = message;
    }
}

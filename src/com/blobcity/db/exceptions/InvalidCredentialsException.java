/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.exceptions;

/**
 *
 * @author sanketsarang
 */
public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(){
        super("Invalid Credentials");
    }
    
    public InvalidCredentialsException(String message){
        super(message);
    }
}

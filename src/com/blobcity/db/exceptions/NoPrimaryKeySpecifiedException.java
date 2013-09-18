/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.exceptions;

/**
 * Exception that indicates a primary key not being specified for an operation
 * that mandatorily requires a primary key
 * 
 * @author sanketsarang
 */
public class NoPrimaryKeySpecifiedException extends Exception{
    
    public NoPrimaryKeySpecifiedException(){
        super("Primary key not specified");
    }
    
    public NoPrimaryKeySpecifiedException(String message){
        super(message);
    }
}

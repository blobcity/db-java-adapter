/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.exceptions;

/**
 * Exception indicating that a record already exists for the primary key
 * 
 */
public class RecordExistsException extends Exception{
    public RecordExistsException(){
        super("Record already exists");
    }
    
    public RecordExistsException(String message){
        super(message);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.exceptions;

/**
 * Exception that indicates an index field not being specified for an operation
 * that mandatorily requires a indexed field. The index field missing maybe a 
 * primary key.
 * 
 * @author sanketsarang
 */
public class NoIndexSpecifiedException extends Exception{
    public NoIndexSpecifiedException(){
        super("No index specified");
    }
    
    public NoIndexSpecifiedException(String message){
        super(message);
    }
}

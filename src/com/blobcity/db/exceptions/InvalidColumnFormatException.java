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
public class InvalidColumnFormatException extends Exception{
    public InvalidColumnFormatException(){
        super("Format of the table is incorrect");
    }
    
    public InvalidColumnFormatException(String message){
        super(message);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.exceptions;

/**
 *
 * @author sanketsarang
 */
public class InvalidEntityException extends Exception{
    public InvalidEntityException(){
        super("Invalid Entity");
    }
    
    public InvalidEntityException(String message){
        super(message);
    }
}

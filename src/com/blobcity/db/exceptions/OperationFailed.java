/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.exceptions;

/**
 *
 * @author Karishma
 */
public class OperationFailed extends Exception {
     public OperationFailed(){
        super("Operation failed");
    }
    
    public OperationFailed(String message){
        super(message);
    }
    
}

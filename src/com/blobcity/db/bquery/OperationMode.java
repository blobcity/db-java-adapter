/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */

package com.blobcity.db.bquery;

/**
 * Represents modes of operation of the adapter.<br/>
 * 
 * <b>HTTP</b>: The adapter will use http calls to communicate with database<br/>
 * <b>REMOTE_EJB</b>: The adpater will use remote REMOTE_EJB calls to communication with database. This usually
 * occurs when the database is hosted in a central location but it's remote interface can access the EJB from the
 * same deployed instance.<br/>
 * 
 * @author Sanket Sarang
 * @version 1.0
 */
public enum OperationMode {
    HTTP, REMOTE_EJB
}

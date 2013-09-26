
package com.blobcity.db.constants;

/**
 * Represents modes of operation of the adapter.<br/>
 * 
 * <b>HTTP</b>: The adapter will use http calls to communicate with database<br/>
 * <b>EJB</b>: The adpater will use remote EJB calls to communication with database.
 * This mode is activated only the application is deployed on BlobCity servers
 * 
 * @author Sanket Sarang <sanket@blobcity.net>
 * @version 1.0
 */
public enum OperationMode {
    HTTP, EJB
}

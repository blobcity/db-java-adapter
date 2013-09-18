/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.constants;

/**
 * Defines the various connection modes that are possible for connecting to the
 * BlobCity Cloud Storage System
 * <p>Global: Used when code running on developers server intends to connect
 * to the data store<br/>
 * User: Used when code running on user's hardware intends to directly
 * connection to the data store.</p>
 * 
 * @author sanketsarang
 */
public enum ConnectionMode {
    GLOBAL, USER
}

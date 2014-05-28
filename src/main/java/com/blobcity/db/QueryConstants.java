/*
 * Copyright 2013 - 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

/**
 * Class to hold query parameter names for the adapter's communication
 *
 * @author Karun AB <karun.ab@blobcity.com>
 */
class QueryConstants {

    /* JSON BlobCity Credentials and Entity notations */
    public static final String USERNAME = "user";
    public static final String PASSWORD = "pass";
    public static final String DB = "db";

    public static final String TABLE = "t";
    public static final String QUERYTYPE = "q";
    public static final String PRIMARY_KEY = "pk";

    /* Payload */
    public static final String PAYLOAD = "p";

    /* Query response
     * ack=0 (Operation successful)
     * ack=1 (Operation failed)
     */
    public static final String ACK = "ack";

    /* Cause of failure of the operation */
    public static final String CAUSE = "cause";

}

/*
 * Copyright 2013 - 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

/**
 * Class to hold QUERY parameter names for the adapter's communication
 *
 * @author Karun AB <karun.ab@blobcity.com>
 */
class QueryConstants {

    /* Request data key */
    public static final String TABLE = "t";
    public static final String QUERY = "q";
    public static final String PRIMARY_KEY = "pk";

    /* Response data keys */
    public static final String PAYLOAD = "p";
    public static final String ACK = "ack"; // 1=success; 0=failure
    public static final String CODE = "code";
    public static final String CAUSE = "cause";
    public static final String KEYS = "keys";
    public static final String CONTAINS = "contains";
}

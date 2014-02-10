/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

/**
 * Enumeration of conditional operators that can be applied on different filter criteria defined by {@link SearchParam}
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
public enum SearchOperator {

    /**
     * And condition for queries
     */
    AND,
    /**
     * Or condition for queries
     */
    OR;
}

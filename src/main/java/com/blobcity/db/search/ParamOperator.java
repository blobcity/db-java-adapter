/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import com.blobcity.db.exceptions.InternalDbException;

/**
 * Enumeration of operators that can be applied inside search criteria defined by {@link SearchParam}
 *
 * @author Karun AB
 */
public enum ParamOperator implements Sqlable {

    /**
     * Contains like query, expects a list of parameters. If the type of data doesn't match the value of the source on which it is being applied (say an integer
     * is checked with string "a"), an {@link InternalDbException} will be thrown.
     */
    IN,
    /**
     * Equals check, expects 1 parameter
     */
    EQ("="),
    /**
     * Not equal check, expects 1 parameter
     */
    NOT_EQ("<>"),
    /**
     * Less than check, expects 1 parameter
     */
    LT("<"),
    /**
     * Greater than check, expects 1 parameter
     */
    GT(">"),
    /**
     * Less than or equals check, expects 1 parameter
     */
    LT_EQ("<="),
    /**
     * Greater than or equals check, expects 1 parameter
     */
    GT_EQ(">="),
    /**
     * between check, expects 2 parameter. To be applied on types which support range searches such as {@link Integer}s and {@link Long}s.
     */
    BETWEEN;
    private String sqlText;

    private ParamOperator() {
        // do nothing
    }

    private ParamOperator(final String sqlText) {
        this.sqlText = sqlText;
    }

    @Override
    public String asSql() {
        return sqlText == null ? name() : sqlText;
    }
}

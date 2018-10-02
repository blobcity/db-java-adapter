/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

/**
 * Enumeration of conditional operators that can be applied on different filter criteria defined by {@link SearchParam}
 *
 * @author Karun AB
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

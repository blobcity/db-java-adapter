/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

/**
 * A interface denoting the implementer is ready to provide output for a {@link Query} or any of it's sub elements in SQL format.
 * <p>
 * Usually implemented along with {@link Jsonable}.
 * <p>
 * This interface has default visibility since its visibility is to be restricted to its package.
 *
 * @author Karun AB
 * @author Sanket Sarang
 * @see Jsonable
 */
interface Sqlable {

  /**
   * Provides a legal SQL form of the implementing class
   *
   * @return instance of a legal SQL format as a (@link String}
   */

  public String asSql();

  /**
   * Provides a legal SQL form of the implementing class with the generated SQL being for the specified datastore
   *
   * @param ds name of datastore
   * @return instance of a legal SQL format as a {@link String}
   */
  public String asSql(final String ds);
}

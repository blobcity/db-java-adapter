/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * A interface denoting the implementer is ready to provide output for a {@link Query} or any of it's sub elements in JSON format. {@code T} represents
 * {@link JsonArray} or {@link JsonObject} based on the usage (since they do not have common parentage). Specific implementations like {@link ArrayJsonable} and
 * {@link ObjectJsonable} provide specific implementations for ease of consumption.
 * <p>
 * Usually goes along with {@link Sqlable}.
 * <p>
 * This interface has default visibility since its visibility is to be restricted to its package.
 *
 * @param <T> Class on which the query is being performed
 * @author Karun AB
 * @see ArrayJsonable
 * @see ObjectJsonable
 * @see Sqlable
 */
interface Jsonable<T> {

  /**
   * Provides a legal JSON form of the implementing class
   *
   * @return instance of a legal JSON format (as {@link org.json.JSONArray}, {@link org.json.JSONObject} or {@link String}) as defined by {@code T}
   */
  public T asJson();
}

/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search.interfaceType;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A interface denoting the implementer is ready to provide output for a {@link Query} or any of it's sub elements in JSON format. {@code T} represents
 * {@link JSONArray} or {@link JSONObject} based on the usage (since they do not have common parentage). Specific implementations like {@link ArrayJsonable} and
 * {@link ObjectJsonable} provide specific implementations for ease of consumption.
 *
 * Usually goes along with {@link Sqlable}
 *
 * @see ArrayJsonable
 * @see ObjectJsonable
 * @see Sqlable
 * @author Karun AB <karun.ab@blobcity.net>
 */
public interface Jsonable<T> {

    /**
     * Provides a legal JSON form of the implementing class
     *
     * @return instance of a legal JSON format (as {@link JSONArray}, {@link JSONObject} or {@link String}) as defined by {@code T}
     */
    public T asJson();
}

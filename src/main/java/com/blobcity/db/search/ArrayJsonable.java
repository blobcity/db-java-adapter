/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import com.google.gson.JsonArray;

/**
 * Implementation of {@link Jsonable} which provides a {@link JsonArray} as an output.
 *
 * This interface has default visibility since its visibility is to be restricted to its package.
 *
 * @author Karun AB
 */
interface ArrayJsonable extends Jsonable<JsonArray> {
}

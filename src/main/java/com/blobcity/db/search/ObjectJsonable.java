/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import com.google.gson.JsonObject;

/**
 * Implementation of {@link Jsonable} which provides a {@link JsonObject} as an output.
 * <p>
 * This interface has default visibility since its visibility is to be restricted to its package.
 *
 * @author Karun AB
 */
interface ObjectJsonable extends Jsonable<JsonObject> {
}

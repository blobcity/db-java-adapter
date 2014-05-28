/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import org.json.JSONObject;

/**
 * Implementation of {@link Jsonable} which provides a {@link JSONObject} as an output.
 *
 * This interface has default visibility since its visibility is to be restricted to its package.
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
interface ObjectJsonable extends Jsonable<JSONObject> {
}

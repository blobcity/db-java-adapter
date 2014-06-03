/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import org.json.JSONArray;

/**
 * Implementation of {@link Jsonable} which provides a {@link JSONArray} as an output.
 * 
 * This interface has default visibility since its visibility is to be restricted to its package.
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
interface ArrayJsonable extends Jsonable<JSONArray> {
}

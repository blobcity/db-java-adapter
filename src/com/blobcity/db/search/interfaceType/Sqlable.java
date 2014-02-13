/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search.interfaceType;

/**
 * A interface denoting the implementer is ready to provide output for a {@link Query} or any of it's sub elements in SQL format.
 *
 * Usually implemented along with {@link Jsonable}
 *
 * @see Jsonable
 * @author Karun AB <karun.ab@blobcity.net>
 */
public interface Sqlable {

    /**
     * Provides a legal SQL form of the implementing class
     *
     * @return instance of a legal SQL format as a {@link String}
     */
    public String asSql();
}

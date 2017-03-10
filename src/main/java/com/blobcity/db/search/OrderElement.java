/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import com.google.gson.JsonObject;

/**
 * Class to handle order by clauses for results of queries. Instances of this class are immutable. Allows cloning if required.
 *
 * @author Karun AB
 */
public class OrderElement implements ObjectJsonable, Sqlable, Cloneable {

    private final String columnName;
    private final Order order;

    /**
     * Internal constructor. Use {@link #create(java.lang.String, com.blobcity.db.search.Order)} statically for access
     *
     * @see #create(java.lang.String, com.blobcity.db.search.Order)
     * @param columnName name of the column to be ordered
     * @param order direction of ordering
     */
    private OrderElement(final String columnName, final Order order) {
        this.columnName = columnName;
        this.order = order;
    }

    /**
     * Creates an instance of an {@link OrderElement} to define the sort order for the results of a {@link Query}
     *
     * @param columnName name of the column to be ordered
     * @param order direction of ordering
     * @return an instantiated instance of {@link OrderElement}
     */
    public static OrderElement create(final String columnName, final Order order) {
        return new OrderElement(columnName, order);
    }

    @Override
    public JsonObject asJson() {
        final JsonObject orderData = new JsonObject();
        orderData.addProperty(columnName, order.name());
        return orderData;
    }

    @Override
    public String asSql() {
        return columnName + " " + order;
    }

    @Override
    public String asSql(final String ds) {
        throw new RuntimeException("Incorrect invocation. Sqlable.asSql(ds) should not be invoked by OrderElement class");
    }

    @Override
    protected OrderElement clone() {
        return new OrderElement(columnName, order);
    }
}

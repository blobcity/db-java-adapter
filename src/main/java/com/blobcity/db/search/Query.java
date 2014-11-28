/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import com.blobcity.db.CloudStorage;
import com.blobcity.db.exceptions.InternalAdapterException;
import com.blobcity.db.exceptions.InternalDbException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Query builder interface for the adapter to support querying mechanism for search functionality
 *
 * @param <T> Class on which the query is being performed
 * @see CloudStorage#search(java.lang.Class, com.blobcity.db.search.SearchParam)
 * @author Karun AB <karun.ab@blobcity.net>
 */
public class Query<T extends CloudStorage> implements ObjectJsonable, Sqlable {

    private final List<String> selectColumnNames;
    private final List<Class<T>> fromTables;
    private SearchParam whereParam;
    private List<String> filterNames;
    private List<OrderElement> orderByList;
    private Integer limit;
    private Integer limitOffset;

    /**
     * Internal constructor. For access, use {@link #select()}, {@link #select(java.lang.String[])} or
     * {@link #table(java.lang.Class)}
     *
     * @see #select()
     * @see #select(java.lang.String[])
     * @see #table(java.lang.Class)
     * @param selectColumnNames names of columns to be selected from the table(s) on which the query is being run
     */
    private Query(final List<String> selectColumnNames) {
        this.selectColumnNames = selectColumnNames;
        this.fromTables = new ArrayList<Class<T>>();
    }

    /**
     * Static initializer for selecting all columns
     *
     * @return an instance of {@link Query} initialized to pick up data from all columns
     */
    public static Query select() {
        return select((String[]) null);
    }

    /**
     * Static initializer for selected columns
     *
     * @param columnNames names of columns to be selected from the table(s) on which the query is being run
     * @return an instance of {@link Query} initialized to pick up data from specified columns
     */
    public static Query select(final String... columnNames) {
        return new Query(columnNames != null && columnNames.length > 0 ? Arrays.asList(columnNames) : Collections.EMPTY_LIST);
    }
    
    public static Query count() {
        return select("COUNT(*)");
    }

    /**
     * Static initializer for selecting data from all columns and setting the table. Internally uses
     * {@link Query#select()} and {@link Query#from(java.lang.Class)}
     *
     * @see #select()
     * @see #from(java.lang.Class)
     * @param <T> instance of {@link CloudStorage}
     * @param tableName name of the table being queried
     * @return an instance of {@link Query}
     */
    public static <T extends CloudStorage> Query table(Class<T> tableName) {
        return Query.select().from(tableName);
    }

    /**
     * Method to add a table to the list of tables on which the query is to be performed. Joins are performed L-R
     * (FIFO).
     *
     * Note, currently joins are not supported. Calling this method repeatedly will cause an {@link InternalDbException}
     * to be thrown.
     *
     * @param tableName name of the table to be queried
     * @return an instance of {@link Query}
     */
    public Query from(Class<T> tableName) {
        if (!fromTables.isEmpty()) {
            throw new InternalDbException("Joins are currently not supported");
        }

        fromTables.add(tableName);
        return this;
    }

    public Query where(SearchParam searchParam) {
        this.whereParam = searchParam;
        return this;
    }

    public Query filter(final String... filterNames) {
        this.filterNames = Arrays.asList(filterNames);
        return this;
    }

    public Query orderBy(final OrderElement... orderElems) {
        this.orderByList = Arrays.asList(orderElems);
        return this;
    }

    /**
     * Use to apply a limit clause on a search query. The result size will be limited to the limit value passed
     *
     * @param limit the number of items to which the search response is to be limited
     * @return a modified object of {@link Query} with the limit clause applied
     */
    public Query limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Use to apply a limit with offset clause on search results. The result will be returned from the offset specified
     * from the final result set and will be limited to the limit parameter.
     *
     * @param limit the number of items to which the search response is to be limited
     * @param offset the number of items skipped from the beginning of the result set
     * @return a modified object of {@link Query} with the limit clause applied
     */
    public Query limit(int limit, int offset) {
        this.limit = limit;
        this.limitOffset = offset;
        return this;
    }

    @Override
    public JsonObject asJson() {
        final JsonObject query = new JsonObject();
        // Select
        if (selectColumnNames != null && !selectColumnNames.isEmpty()) {
            final JsonArray selectColumns = new JsonArray();
            for (final String selectColumn : selectColumnNames) {
                selectColumns.add(new JsonPrimitive(selectColumn));
            }
            query.add("select", selectColumns);
        }

        // From
        if (fromTables == null && fromTables.isEmpty()) {
            throw new InternalAdapterException("No table name set. Table name is a mandatory field queries.");
        }
        final JsonArray tableNames = new JsonArray();
        for (Class<T> tableClazz : fromTables) {
            final String tableName = CloudStorage.getDbName(tableClazz) + "." + CloudStorage.getTableName(tableClazz);

            tableNames.add(new JsonPrimitive(tableName));
        }
        query.add("t", tableNames);

        // Where
        if (whereParam != null) {
            query.add("where", whereParam.asJson());
        }

        // Filter
        if (filterNames != null && !filterNames.isEmpty()) {
            final JsonArray filterElements = new JsonArray();
            for (final String filterName : filterNames) {
                filterElements.add(new JsonPrimitive(filterName));
            }
            query.add("filter", filterElements);
        }

        // Order By
        if (orderByList != null && !orderByList.isEmpty()) {
            final JsonArray orderByElements = new JsonArray();
            for (final OrderElement orderElem : orderByList) {
                orderByElements.add(orderElem.asJson());
            }

            query.add("order-by", orderByElements);
        }

        // Limit clause
        if (limit != null) {
            JsonObject limitJson = new JsonObject();
            limitJson.addProperty("lim", limit);
            if (limitOffset != null) {
                limitJson.addProperty("off", limitOffset);
            }
            query.add("lim", limitJson);
        }

        return query;
    }

    @Override
    public String asSql() {
        final StringBuffer sb = new StringBuffer();
        sb.append("SELECT ").append(StringUtil.join(selectColumnNames, ", ", "*", "`"));

        if (fromTables == null || fromTables.isEmpty()) {
            throw new InternalAdapterException("No table name set. Table name is a mandatory field queries.");
        }

        sb.append(" FROM ");
        final int fromTableCount = fromTables.size();
        for (int i = 0; i < fromTableCount; i++) {
            sb.append('`').append(CloudStorage.getDbName(fromTables.get(i))).append("`.`").append(CloudStorage.getTableName(fromTables.get(i)));

            if (i < fromTableCount - 1) {
                sb.append("`, `");
            }
        }
        sb.append('`');

        if (whereParam != null) {
            sb.append(" WHERE ").append(whereParam.asSql());
        }

        if (orderByList != null && !orderByList.isEmpty()) {
            sb.append(" ORDER BY ");

            final int orderByListSize = orderByList.size();
            for (int i = 0; i < orderByListSize; i++) {
                sb.append(orderByList.get(i).asSql());

                if (i < orderByListSize - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * @return {@link List} of tables being searched through
     */
    public List<Class<T>> getFromTables() {
        return fromTables;
    }
}

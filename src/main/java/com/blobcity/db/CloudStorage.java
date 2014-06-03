/**
 * Copyright 2011 - 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.search.SearchParam;
import com.blobcity.db.annotations.Entity;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.annotations.Primary;
import com.blobcity.db.exceptions.DbOperationException;
import com.blobcity.db.exceptions.InternalAdapterException;
import com.blobcity.db.exceptions.InternalDbException;
import com.blobcity.db.search.Query;
import com.blobcity.db.search.StringUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides the connection and query execution framework for performing operations on the BlobCity data store. This class must be extended by any
 * Model that represents a BlobCity Entity.
 *
 * @author Sanket Sarang
 * @author Karishma
 * @author Karun AB <karun.ab@blobcity.net>
 * @version 1.0
 * @since 1.0
 */
public abstract class CloudStorage {

    private String table = null;
    private String db = null;

    public CloudStorage() {
        for (Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation instanceof Entity) {
                final Entity blobCityEntity = (Entity) annotation;
                table = blobCityEntity.table();
                if (StringUtil.isEmpty(blobCityEntity.db())) {
                    db = blobCityEntity.db();
                }

                if (StringUtil.isEmpty(table)) {
                    table = this.getClass().getSimpleName();
                }
                break;
            }
        }

        if (table == null) {
            table = this.getClass().getSimpleName();
        }

        TableStore.getInstance().registerClass(table, this.getClass());
    }

    // Public static methods
    public static <T extends CloudStorage> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }

    public static <T extends CloudStorage> T newInstance(Class<T> clazz, Object pk) {
        try {
            T obj = clazz.newInstance();
            obj.setPk(pk);
            return obj;
        } catch (InstantiationException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }

    public static <T extends CloudStorage> T newLoadedInstance(Class<T> clazz, Object pk) {
        try {
            T obj = clazz.newInstance();
            obj.setPk(pk);
            if (obj.load()) {
                return obj;
            }
            return null;
        } catch (InstantiationException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }

    public static <T extends CloudStorage> List<Object> selectAll(Class<T> clazz) {
        return selectAll(clazz, Object.class);
    }

    public static <T extends CloudStorage, P extends Object> List<P> selectAll(final Class<T> clazz, final Class<P> returnTypeClazz) {
        JSONObject responseJson = postStaticRequest(Credentials.getInstance(), clazz, QueryType.SELECT_ALL);
        JSONArray jsonArray;
        List<P> list;

        try {
            if (responseJson.getInt(QueryConstants.ACK) == 1) {
                jsonArray = responseJson.getJSONArray(QueryConstants.KEYS);
                list = new ArrayList<P>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(dataTypeTransform((P) jsonArray.getString(i), returnTypeClazz));
                }
                return list;
            }

            throw new DbOperationException(responseJson.getString(QueryConstants.CODE), responseJson.optString(QueryConstants.CAUSE));
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    public static <T extends CloudStorage> boolean contains(final Object key) {
        return contains(Credentials.getInstance(), key);
    }

    public static <T extends CloudStorage> boolean contains(final Credentials credentials, final Object key) {
        JSONObject responseJson = postStaticRequest(credentials, QueryType.CONTAINS, key);

        try {
            if ("1".equals(responseJson.getString(QueryConstants.ACK))) {
                return responseJson.getBoolean("contains");
            }

            throw new DbOperationException(responseJson.getString(QueryConstants.CODE), responseJson.optString(QueryConstants.CAUSE));
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    public static <T extends CloudStorage> void remove(final Object pk) {
        remove(Credentials.getInstance(), pk);
    }

    public static <T extends CloudStorage> void remove(final Credentials credentials, final Object pk) {
        JSONObject responseJson = postStaticRequest(credentials, QueryType.REMOVE, pk);

        try {
            if ("0".equals(responseJson.getString(QueryConstants.ACK))) {
                throw new DbOperationException(responseJson.getString(QueryConstants.CODE), responseJson.optString(QueryConstants.CAUSE));
            }
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    /**
     * Allows search queries to be performed as defined by {@link http://docs.blobcity.com/display/DB/Operations+on+data#Operationsondata-SEARCH}. This method
     * internally calls {@link #search(com.blobcity.db.config.Credentials, com.blobcity.db.search.Query)} with default credentials from
     * {@link Credentials#getInstance()}
     *
     * Note: This return type is prone to update when support for multiple table queries (joins) is introduced.
     *
     * @see #search(com.blobcity.db.config.Credentials, com.blobcity.db.search.Query)
     * @see Credentials#getInstance()
     * @param <T> Any class reference which extends {@link CloudStorage}
     * @param query {@link SearchParam}s which are to be used to search for data
     * @return {@link List} of {@code T} that matches {@code searchParams}
     */
    public static <T extends CloudStorage> List<T> search(final Query<T> query) {
        return search(Credentials.getInstance(), query);
    }

    /**
     * Allows search queries to be performed as defined by {@link http://docs.blobcity.com/display/DB/Operations+on+data#Operationsondata-SEARCH}.
     *
     * Note: This return type is prone to update when support for multiple table queries (joins) is introduced.
     *
     * @param <T> Any class reference which extends {@link CloudStorage}
     * @param credentials Credentials to be used for communicating with the database
     * @param query {@link SearchParam}s which are to be used to search for data
     * @return {@link List} of {@code T} that matches {@code searchParams}
     */
    public static <T extends CloudStorage> List<T> search(final Credentials credentials, final Query<T> query) {
        if (query.getFromTables() == null && query.getFromTables().isEmpty()) {
            throw new InternalAdapterException("No table name set. Table name is a mandatory field queries.");
        }

        final String queryStr = query.asSql();

        final String responseString = QueryExecuter.executeSql(DbQueryRequest.create(credentials, queryStr));

        final JSONObject responseJson;
        try {
            responseJson = new JSONObject(responseString);
        } catch (JSONException ex) {
            throw new InternalDbException("Error in processing request/response JSON", ex);
        }

        try {
            final Class<T> clazz = query.getFromTables().get(0);

            if ("1".equals(responseJson.getString(QueryConstants.ACK))) {
                final JSONArray resultJsonArray = responseJson.getJSONArray(QueryConstants.PAYLOAD);
                final int resultCount = resultJsonArray.length();
                final List<T> responseList = new ArrayList<T>();
                final String tableName = CloudStorage.getTableName(clazz);
                TableStore.getInstance().registerClass(tableName, clazz);
                final Map<String, Field> structureMap = TableStore.getInstance().getStructure(tableName);

                for (int i = 0; i < resultCount; i++) {
                    final T instance = CloudStorage.newInstance(clazz);
                    final JSONObject instanceData = resultJsonArray.getJSONObject(i);
                    final Iterator<String> columnNameIterator = instanceData.keys();

                    while (columnNameIterator.hasNext()) {
                        final String columnName = columnNameIterator.next();

                        final Field field = structureMap.get(columnName);
                        final boolean oldAccessibilityValue = field.isAccessible();
                        field.setAccessible(true);
                        try {
                            field.set(instance, getCastedValue(field, instanceData.get(columnName), clazz));
                        } catch (JSONException ex) {
                            throw new InternalDbException("Error in processing JSON. Class: " + clazz + " Request: " + instanceData.toString(), ex);
                        } catch (IllegalArgumentException ex) {
                            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
                        } catch (IllegalAccessException ex) {
                            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
                        } finally {
                            field.setAccessible(oldAccessibilityValue);
                        }
                    }
                    responseList.add(instance);
                }
                return responseList;
            }

            throw new DbOperationException(responseJson.getString(QueryConstants.CODE), responseJson.optString(QueryConstants.CAUSE));
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    /**
     * Allows quick search queries on a single column. This method internally uses {@link #search(com.blobcity.db.search.Query) }
     *
     * @see #search(com.blobcity.db.search.Query)
     * @param <T> Any class reference which extends {@link CloudStorage}
     * @param clazz class reference who's data is to be searched
     * @param columnName column to be searched
     * @param values values to be used to filter data in column
     * @return {@link List} of {@code T} that matches {@code searchParams}
     */
    public static <T extends CloudStorage> List<T> select(final Class<T> clazz, final String columnName, final Object... values) {
        return search(Query.table(clazz).where(SearchParam.create(columnName).in(values)));
    }

    public static <T extends CloudStorage> List<T> filter(Class<T> clazz, String filterName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Statically provides the table name for any instance/child of {@link CloudStorage} that is internally used by the adapter for querying. Note, this method
     * is not used by the adapter internally but the logic here, should be kept in sync with the rest of the class to ensure table names are evaluated
     * appropriately. This method can be used for logging purposes where the table name for a class is required.
     *
     * @param <T> Any class reference which extends {@link CloudStorage}
     * @param clazz class reference who's table name is required
     * @return Name of the table
     */
    public static <T extends CloudStorage> String getTableName(final Class<T> clazz) {
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        return entity != null && !StringUtil.isEmpty(entity.table()) ? entity.table() : clazz.getSimpleName();
    }

    /**
     * Statically provides the db name for any instance/child of {@link CloudStorage} that is internally used by the adapter for querying. Note, this method is
     * used by the adapter internally for SQL queries and the logic here should be kept in sync with the rest of the class to ensure db names are evaluated
     * appropriately. This method can be used for logging purposes where the db name for a class is required.
     *
     * @param <T> Any class reference which extends {@link CloudStorage}
     * @param clazz class reference who's db name is required
     * @return Name of the DB
     */
    public static <T extends CloudStorage> String getDbName(final Class<T> clazz) {
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        return entity != null && !StringUtil.isEmpty(entity.db()) ? entity.db() : Credentials.getInstance().getDb();
    }

    public static <T extends CloudStorage> Object invokeProc(final String storedProcedureName, final String... params) {
        return invokeProc(Credentials.getInstance(), storedProcedureName, params);
    }

    // TODO: Complete method implementation This method is clearly not complete.
    public static <T extends CloudStorage> Object invokeProc(final Credentials credentials, final String storedProcedureName, final String... params) {
        final JSONObject responseJson = postStaticProcRequest(credentials, QueryType.STORED_PROC, storedProcedureName, params);
        try {

            /* If ack:0 then check for error code and report accordingly */
            if ("0".equals(responseJson.getString(QueryConstants.ACK))) {
                final String cause = responseJson.optString(QueryConstants.CAUSE);
                final String code = responseJson.optString(QueryConstants.CODE);

                throw new DbOperationException(code, cause);
            }

            final Object payloadObj = responseJson.get(QueryConstants.PAYLOAD);
            if (payloadObj instanceof CloudStorage) {
                return "1 obj";
            } else if (payloadObj instanceof JSONArray) {
                return ((JSONArray) payloadObj).length() + " objs";
            }

            return payloadObj;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    // Public instance methods
    public boolean load() {
        return load(Credentials.getInstance());
    }

    public void save() {
        save(Credentials.getInstance());
    }

    public boolean insert() {
        return insert(Credentials.getInstance());
    }

    public void remove() {
        remove(Credentials.getInstance());
    }

    /**
     * Gets a JSON representation of the object. The column names are same as those loaded in {@link TableStore}
     *
     * @return {@link JSONObject} representing the entity class in its current state
     */
    public JSONObject asJson() {
        try {
            return toJson();
        } catch (IllegalArgumentException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }

    // Protected instance methods
    protected void setPk(Object pk) {
        final Field primaryKeyField = TableStore.getInstance().getPkField(table);
        final boolean accessible = primaryKeyField.isAccessible();

        try {
            if (!accessible) {
                primaryKeyField.setAccessible(true);
            }

            primaryKeyField.set(this, pk);
        } catch (IllegalArgumentException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } finally {
            if (!accessible) {
                primaryKeyField.setAccessible(false);
            }
        }
    }

    // Private static methods
    private static <T extends CloudStorage> JSONObject postStaticProcRequest(final Credentials credentials, final QueryType queryType, final String name, final String[] params) {
        final Map<String, Object> queryParamMap = new HashMap<String, Object>();
        queryParamMap.put(QueryConstants.QUERY, queryType.getQueryCode());

        final Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("name", name);
        paramsMap.put("params", new JSONArray(params != null ? Arrays.asList(params) : null));

        queryParamMap.put(QueryConstants.PAYLOAD, new JSONObject(paramsMap));

        final String responseString = QueryExecuter.executeBql(DbQueryRequest.create(credentials, new JSONObject(queryParamMap).toString()));
        try {
            final JSONObject responseJson = new JSONObject(responseString);
            return responseJson;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in processing request/response JSON", ex);
        }
    }

    /**
     * Transforms data type of a column dynamically leveraging Java Type Erasure. Currently supports all types that can be used as primary keys in tables.
     *
     * @param <P> Requested data format class parameter
     * @param value value to be transformed
     * @param returnTypeClazz Class object in who's image the {@code value} has to be transformed
     * @return transformed data object to an appropriate type
     */
    private static <P extends Object> P dataTypeTransform(final P value, final Class<P> returnTypeClazz) {
        if (returnTypeClazz == Integer.class) {
            return (P) Integer.valueOf(value.toString());
        }

        if (returnTypeClazz == Float.class) {
            return (P) Float.valueOf(value.toString());
        }

        if (returnTypeClazz == Long.class) {
            return (P) Long.valueOf(value.toString());
        }

        if (returnTypeClazz == Double.class) {
            return (P) Double.valueOf(value.toString());
        }

        // String
        return value;
    }

    /**
     * Provides a standard service to cast input types from JSON's format ({@link Integer}, {@link String}, {@link JSONArray} etc.) to Java's internal data
     * types.
     *
     * @param field field in current {@link Object} that needs to be updated
     * @param value value to be set for the field
     * @param parentClazz {@link Class} value of the parent object for which the casted field is being requested. This field is only required for proper error
     * logging in case of exceptions
     * @return appropriately casted value
     */
    private static Object getCastedValue(final Field field, final Object value, final Class<?> parentClazz) {
        final Class<?> type = field.getType();

        if (type == String.class) { // Pre-exit most common use cases
            if (value.getClass() == JSONObject.NULL.getClass()) {
                return null;
            }

            return value;
        }

        if (type.isEnum()) {
            return "".equals(value.toString()) ? null : Enum.valueOf((Class<? extends Enum>) type, value.toString());
        }

        if (type == Double.TYPE || type == Double.class) {
            return new Double(value.toString());
        }

        if (type == Float.TYPE || type == Float.class) {
            return new Float(value.toString());
        }

        if (type == Character.TYPE || type == Character.class) {
            return value.toString().charAt(0);
        }

        if (type == Boolean.TYPE || type == Boolean.class) {
            return Boolean.valueOf(value.toString());
        }

        if (type == BigDecimal.class) {
            return new BigDecimal(value.toString());
        }

        if (type == java.util.Date.class) {
            return new java.util.Date(Long.valueOf(value.toString()));
        }

        if (type == java.sql.Date.class) {
            return new java.sql.Date(Long.valueOf(value.toString()));
        }

//        Note: This code is unnecessary but is kept here to show that these values are supported and if tomorrow,
//        the return type of the DB changes to String instead of an int/long in JSON, this code shold be uncommented
//
//        if (type == Integer.TYPE || type == Integer.class) { // should be unnecessary
//            return new Integer(value.toString());
//        }
//
//        if (type == Long.TYPE || type == Long.class) { // should be unnecessary
//            return new Long(value.toString());
//        }
        if (type == List.class) { // doesn't always return inside this block, BEWARE!
            if (value instanceof JSONArray) {
                final JSONArray arr = (JSONArray) value;
                final int length = arr.length();
                final List<Object> list = new ArrayList(length);

                for (int i = 0; i < length; i++) {
                    list.add(arr.opt(i));
                }
                return list;
            } else if ((value instanceof String && "".equals(value)) || value.getClass() == JSONObject.NULL.getClass()) {
                return new ArrayList();
            }

            Logger.getLogger(CloudStorage.class.getName()).log(Level.WARNING, "Class of type \"{0}\" has field with name \"{1}\" and data type \"{2}\" for value to be set was \"{3}\" has a type of {4}. This will probably cause an exception.", new Object[]{parentClazz, field.getName(), type, value, value.getClass()});
        }
        // The if for List check does not always return a value. Be sure before putting any code below here

        // String & any other weird type
        return value;
    }

    private static <T extends CloudStorage> JSONObject postStaticRequest(final Credentials credentials, final Class<T> clazz, final QueryType queryType) {
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);

        final Map<String, Object> requestMap = new HashMap<String, Object>();
        final String tableName = entity != null && entity.table() != null && !"".equals(entity.table()) ? entity.table() : clazz.getSimpleName();

        final boolean entityContainsDbName = entity != null && entity.db() != null && !"".equals(entity.db());
        final String db = entityContainsDbName ? entity.db() : credentials.getDb(); // No NPEs here because entityContainsDbName handles that
        final Credentials dbSpecificCredentials = entityContainsDbName ? Credentials.create(credentials, null, null, null, db) : credentials;

        requestMap.put(QueryConstants.TABLE, tableName);
        requestMap.put(QueryConstants.QUERY, queryType.getQueryCode());
        final String queryStr = new JSONObject(requestMap).toString();

        try {
            final String responseString = QueryExecuter.executeBql(DbQueryRequest.create(dbSpecificCredentials, queryStr));
            final JSONObject responseJson = new JSONObject(responseString);
            return responseJson;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in processing request/response JSON", ex);
        }
    }

    private static <T extends CloudStorage> JSONObject postStaticRequest(final Credentials credentials, final QueryType queryType, final Object pk) {
        try {
            final Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put(QueryConstants.QUERY, queryType.getQueryCode());
            queryMap.put(QueryConstants.PRIMARY_KEY, pk);
            final String queryStr = new JSONObject(queryMap).toString();

            final String responseString = QueryExecuter.executeBql(DbQueryRequest.create(credentials, queryStr));
            final JSONObject responseJson = new JSONObject(responseString);
            return responseJson;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in processing request/response JSON", ex);
        }
    }

    // Private instance methods
    private boolean load(final Credentials credentials) {
        JSONObject responseJson;
        JSONObject payloadJson;
        responseJson = postRequest(credentials, QueryType.LOAD);
        try {

            /* If ack:0 then check for error code and report accordingly */
            if ("0".equals(responseJson.getString(QueryConstants.ACK))) {
                if ("DB200".equals(responseJson.getString(QueryConstants.CODE))) {
                    return false;
                } else {
                    reportIfError(responseJson);
                }
            }

            payloadJson = responseJson.getJSONObject(QueryConstants.PAYLOAD);
            fromJson(payloadJson);
            return true;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    private void save(final Credentials credentials) {
        JSONObject responseJson = postRequest(credentials, QueryType.SAVE);
        reportIfError(responseJson);
    }

    private boolean insert(final Credentials credentials) {
        JSONObject responseJson = postRequest(credentials, QueryType.INSERT);
        try {
            if ("1".equals(responseJson.getString(QueryConstants.ACK))) {
                final JSONObject payloadJson = responseJson.getJSONObject(QueryConstants.PAYLOAD);
                fromJson(payloadJson);
                return true;
            } else if ("0".equals(responseJson.getString(QueryConstants.ACK))) {
                if ("DB201".equals(responseJson.getString(QueryConstants.CODE))) {
                    return false;
                }

                /*
                 * considering conditions before this and the code in {@link #reportIfError(JSONObject)}, this call will always result in an exception.
                 */
                reportIfError(responseJson);
            }

            throw new InternalAdapterException("Unknown acknowledgement code from the database. Expected: [0, 1]. Actual: " + responseJson.getString(QueryConstants.ACK));
        } catch (Exception ex) {
            reportIfError(responseJson);
            throw new InternalAdapterException("Exception occurred in the adapter.", ex);
        }
    }

    private void remove(final Credentials credentials) {
        final JSONObject responseJson = postRequest(credentials, QueryType.REMOVE);
        try {

            /* If ack:0 then check for error code and report accordingly */
            if ("0".equals(responseJson.getString(QueryConstants.ACK)) && !"DB200".equals(responseJson.getString(QueryConstants.CODE))) {
                reportIfError(responseJson);
            }
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    /**
     * Instantiates current object with data from the provided {@link JSONObject}.
     *
     * Every column mentioned in the {@link CloudStorage} instance (as maintained by {@link TableStore}) will be loaded with data. If any of these column name
     * IDs do not exist in the provided {@link JSONObject}, an {@link InternalDbException} will be thrown. If there are any issues whilst reflecting the data
     * into the instance, an {@link InternalAdapterException} will be thrown.
     *
     * If any data already exists the calling object in any field mapped as a column, the data will be overwritten and lost.
     *
     * @param jsonObject input {@link JSONObject} from which the data for the current instance are to be loaded.
     */
    private void fromJson(final JSONObject jsonObject) {
        final Map<String, Field> structureMap = TableStore.getInstance().getStructure(table);

        for (final String columnName : structureMap.keySet()) {
            final Field field = structureMap.get(columnName);

            try {
                setFieldValue(field, jsonObject.get(columnName));
            } catch (JSONException ex) {
                throw new InternalDbException("Error in processing JSON. Class: " + this.getClass() + " Request: " + jsonObject.toString(), ex);
            } catch (IllegalArgumentException ex) {
                throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
            } catch (IllegalAccessException ex) {
                throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
            }
        }
    }

    private JSONObject postRequest(final Credentials credentials, QueryType queryType) {
        JSONObject responseJson;
        try {
            final Map<String, Object> queryParamMap = new HashMap<String, Object>();
            queryParamMap.put(QueryConstants.TABLE, table);
            queryParamMap.put(QueryConstants.QUERY, queryType.getQueryCode());

            final Credentials dbSpecificCredentials = db != null ? Credentials.create(credentials, null, null, null, db) : credentials;

            switch (queryType) {
                case LOAD:
                case REMOVE:
                    queryParamMap.put(QueryConstants.PRIMARY_KEY, getPrimaryKeyValue());
                    break;
                case INSERT:
                case SAVE:
                    queryParamMap.put(QueryConstants.PAYLOAD, toJson());
                    break;
                default:
                    throw new InternalDbException("Attempting to executed unknown or unidentifed query");
            }

            final String queryStr = new JSONObject(queryParamMap).toString();

            final String responseString = QueryExecuter.executeBql(DbQueryRequest.create(dbSpecificCredentials, queryStr));
            responseJson = new JSONObject(responseString);
            return responseJson;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in processing request/response JSON", ex);
        } catch (IllegalArgumentException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }

    /**
     * Gets a JSON representation of the object. The column names are same as those loaded in {@link TableStore}
     *
     * @return {@link JSONObject} representing the entity class in its current state
     * @throws IllegalArgumentException if the specified object is not an instance of the class or interface declaring the underlying field (or a subclass or
     * implementor thereof).
     * @throws IllegalAccessException if this {@code Field} object is enforcing Java language access control and the underlying field is inaccessible.
     */
    private JSONObject toJson() throws IllegalArgumentException, IllegalAccessException {
        final Map<String, Field> structureMap = TableStore.getInstance().getStructure(table);
        final Map<String, Object> dataMap = new HashMap<String, Object>();

        for (String columnName : structureMap.keySet()) {
            final Field field = structureMap.get(columnName);
            final boolean accessible = field.isAccessible();

            field.setAccessible(true);

            try {
                if (field.getType().isEnum()) {
                    dataMap.put(columnName, field.get(this) != null ? field.get(this).toString() : null);
                    continue;
                } else if (field.getType() == java.util.Date.class) {
                    dataMap.put(columnName, field.get(this) != null ? ((java.util.Date) field.get(this)).getTime() : null);
                    continue;
                } else if (field.getType() == java.sql.Date.class) {
                    dataMap.put(columnName, field.get(this) != null ? ((java.sql.Date) field.get(this)).getTime() : null);
                    continue;
                }

                dataMap.put(columnName, field.get(this));
            } catch (IllegalAccessException iae) {
                throw iae;
            } finally {
                field.setAccessible(accessible);
            }
        }

        return new JSONObject(dataMap);
    }

    private void reportIfError(JSONObject jsonObject) {
        try {
            if (!"1".equals(jsonObject.getString(QueryConstants.ACK))) {
                final String code = jsonObject.optString(QueryConstants.CODE);
                final String cause = jsonObject.optString(QueryConstants.CAUSE);

                throw new DbOperationException(code, cause);
            }
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    private Object getPrimaryKeyValue() throws IllegalArgumentException, IllegalAccessException {
        Map<String, Field> structureMap = TableStore.getInstance().getStructure(table);

        for (String columnName : structureMap.keySet()) {
            Field field = structureMap.get(columnName);
            if (field.getAnnotation(Primary.class) != null) {
                final boolean accessible = field.isAccessible();

                field.setAccessible(true);
                try {
                    final Object value = field.get(this);
                    return value;
                } catch (IllegalAccessException iae) {
                    throw iae;
                } finally {
                    field.setAccessible(accessible);
                }
            }
        }

        return null;
    }

    /**
     * Sets field level values by ensuring appropriate conversion between the input type (JSON) and Java's inherent data types.
     *
     * @see #getCastedValue(java.lang.reflect.Field, java.lang.Object)
     * @param field field in current {@link Object} that needs to be updated
     * @param value value to be set for the field
     * @throws IllegalAccessException if the underlying field being changed is final
     */
    private void setFieldValue(final Field field, final Object value) throws IllegalAccessException {
        final boolean oldAccessibilityValue = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(this, getCastedValue(field, value, this.getClass()));
        } catch (IllegalAccessException iae) {
            throw iae;
        } finally {
            field.setAccessible(oldAccessibilityValue);
        }
    }
}

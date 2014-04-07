/**
 * Copyright 2011 - 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.search.SearchParam;
import com.blobcity.db.bquery.QueryExecuter;
import com.blobcity.db.classannotations.Entity;
import com.blobcity.db.constants.Credentials;
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.constants.QueryType;
import com.blobcity.db.exceptions.DbOperationException;
import com.blobcity.db.exceptions.InternalAdapterException;
import com.blobcity.db.exceptions.InternalDbException;
import com.blobcity.db.search.Query;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
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
 * @version 1.0
 * @since 1.0
 */
public abstract class CloudStorage {

    private String table = null;

    public CloudStorage() {
        for (Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation instanceof Entity) {
                Entity blobCityEntity = (Entity) annotation;
                table = blobCityEntity.table();
                if (table == null || "".equals(table)) {
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
        JSONObject responseJson = postStaticRequest(clazz, QueryType.SELECT_ALL);
        JSONArray jsonArray;
        List<P> list;

        try {
            if ("1".equals(responseJson.getString("ack"))) {
                jsonArray = responseJson.getJSONArray("keys");
                list = new ArrayList<P>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(dataTypeTransform((P) jsonArray.getString(i), returnTypeClazz));
                }
                return list;
            }

            throw new DbOperationException(responseJson.getString("code"));
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    public static <T extends CloudStorage> boolean contains(Class<T> clazz, Object key) {
        JSONObject responseJson = postStaticRequest(clazz, QueryType.CONTAINS, key);

        try {
            if ("1".equals(responseJson.getString("ack"))) {
                return responseJson.getBoolean("contains");
            }

            throw new DbOperationException(responseJson.getString("code"));
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    public static <T extends CloudStorage> void remove(Class<T> clazz, Object pk) {
        JSONObject responseJson = postStaticRequest(clazz, QueryType.REMOVE, pk);

        try {
            if (responseJson.getString("ack").equals("0")) {
                throw new DbOperationException(responseJson.getString("code"));
            }
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    /**
     * Allows search queries to be performed as defined by {@link http://docs.blobcity.com/display/DB/Operations+on+data#Operationsondata-SEARCH}.
     *
     * Note: This return type is prone to update when support for multiple table queries (joins) is introduced.
     *
     * @param <T> Any class reference which extends {@link CloudStorage}
     * @param query {@link SearchParam}s which are to be used to search for data
     * @return {@link List} of {@code T} that matches {@code searchParams}
     */
    public static <T extends CloudStorage> List<T> search(Query<T> query) {
        if (query.getFromTables() == null && query.getFromTables().isEmpty()) {
            throw new InternalAdapterException("No table name set. Table name is a mandatory field queries.");
        }

        final Class<T> clazz = query.getFromTables().get(0);

        final Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("app", Credentials.getInstance().getAppId());
        requestMap.put("key", Credentials.getInstance().getAppKey());
        requestMap.put("p", query.asSql());

        final String responseString = new QueryExecuter().executeSql(new JSONObject(requestMap));

        final JSONObject responseJson;
        try {
            responseJson = new JSONObject(responseString);
        } catch (JSONException ex) {
            throw new InternalDbException("Error in processing request/response JSON", ex);
        }

        try {
            if ("1".equals(responseJson.getString("ack"))) {
                final JSONArray resultJsonArray = responseJson.getJSONArray("p");
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

            throw new DbOperationException(responseJson.getString("code"));
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
        return entity != null && entity.table() != null && !"".equals(entity.table()) ? entity.table() : clazz.getSimpleName();
    }

    protected void setPk(Object pk) {
        Field primaryKeyField = TableStore.getInstance().getPkField(table);
        try {
            primaryKeyField.setAccessible(true);
            primaryKeyField.set(this, pk);
            primaryKeyField.setAccessible(false);
        } catch (IllegalArgumentException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }

    public boolean load() {
        JSONObject responseJson;
        JSONObject payloadJson;
        responseJson = postRequest(QueryType.LOAD);
        try {

            /* If ack:0 then check for error code and report accordingly */
            if ("0".equals(responseJson.getString("ack"))) {
                if ("DB200".equals(responseJson.getString("code"))) {
                    return false;
                } else {
                    reportIfError(responseJson);
                }
            }

            payloadJson = responseJson.getJSONObject("p");
            fromJson(payloadJson);
            return true;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }

    public void save() {
        JSONObject responseJson = postRequest(QueryType.SAVE);
        reportIfError(responseJson);
    }

    public boolean insert() {
        JSONObject responseJson = postRequest(QueryType.INSERT);
        try {
            if ("1".equals(responseJson.getString("ack"))) {
                final JSONObject payloadJson = responseJson.getJSONObject("p");
                fromJson(payloadJson);
                return true;
            } else if ("0".equals(responseJson.getString("ack"))) {
                if ("DB201".equals(responseJson.getString("code"))) {
                    return false;
                }

                /*
                 * considering conditions before this and the code in {@link #reportIfError(JSONObject)}, this call will always result in an exception.
                 */
                reportIfError(responseJson);
            }

            throw new InternalAdapterException("Unknown acknowledgement code from the database. Expected: [0, 1]. Actual: " + responseJson.getString("ack"));
        } catch (Exception ex) {
            reportIfError(responseJson);
            throw new InternalAdapterException("Exception occurred in the adapter.", ex);
        }
    }

    public void remove() {
        final JSONObject responseJson = postRequest(QueryType.REMOVE);
        try {

            /* If ack:0 then check for error code and report accordingly */
            if ("0".equals(responseJson.getString("ack")) && !responseJson.getString("code").equals("DB200")) {
                reportIfError(responseJson);
            }
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
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
            field.setAccessible(true);
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

    private JSONObject postRequest(QueryType queryType) {
        JSONObject requestJson;
        JSONObject responseJson;
        try {
            requestJson = new JSONObject();
            requestJson.put("app", Credentials.getInstance().getAppId());
            requestJson.put("key", Credentials.getInstance().getAppKey());
            requestJson.put("t", table);
            requestJson.put("q", queryType.getQueryCode());

            switch (queryType) {
                case LOAD:
                case REMOVE:
                    requestJson.put("pk", getPrimaryKeyValue());
                    break;
                case INSERT:
                case SAVE:
                    requestJson.put("p", toJson());
                    break;
                default:
                    throw new InternalDbException("Attempting to executed unknown or unidentifed query");
            }

            final String responseString = new QueryExecuter().executeQuery(requestJson);
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

    private static <T extends CloudStorage> JSONObject postStaticRequest(Class<T> clazz, QueryType queryType) {
        JSONObject requestJson;
        JSONObject responseJson;
        Entity entity = (Entity) clazz.getAnnotation(Entity.class);

        requestJson = new JSONObject();
        try {
            requestJson.put("app", Credentials.getInstance().getAppId());
            requestJson.put("key", Credentials.getInstance().getAppKey());
            final String tableName = entity != null && entity.table() != null && !"".equals(entity.table()) ? entity.table() : clazz.getSimpleName();
            requestJson.put("t", tableName);
            requestJson.put("q", queryType.getQueryCode());

            final String responseString = new QueryExecuter().executeQuery(requestJson);
            responseJson = new JSONObject(responseString);
            return responseJson;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in processing request/response JSON", ex);
        }
    }

    private static <T extends CloudStorage> JSONObject postStaticRequest(Class<T> clazz, QueryType queryType, Object pk) {
        JSONObject requestJson;
        JSONObject responseJson;
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);

        requestJson = new JSONObject();
        try {
            requestJson.put("app", Credentials.getInstance().getAppId());
            requestJson.put("key", Credentials.getInstance().getAppKey());
            final String tableName = entity != null && entity.table() != null && !"".equals(entity.table()) ? entity.table() : clazz.getSimpleName();
            requestJson.put("t", tableName);
            requestJson.put("q", queryType.getQueryCode());
            requestJson.put("pk", pk);

            final String responseString = new QueryExecuter().executeQuery(requestJson);
            responseJson = new JSONObject(responseString);
            return responseJson;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in processing request/response JSON", ex);
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
            field.setAccessible(true);
            if (field.getType().isEnum()) {
                dataMap.put(columnName, field.get(this) != null ? field.get(this).toString() : null);
                continue;
            }

            dataMap.put(columnName, field.get(this));
        }

        return new JSONObject(dataMap);
    }

    private void reportIfError(JSONObject jsonObject) {
        try {
            if (!"1".equals(jsonObject.getString("ack"))) {
                String cause = "";
                String code = "";

                if (jsonObject.has("code")) {
                    code = jsonObject.getString("code");
                }

                if (jsonObject.has("cause")) {
                    cause = jsonObject.getString("cause");
                }

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
                field.setAccessible(true);
                return field.get(this);
            }
        }

        return null;
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
        field.set(this, getCastedValue(field, value, this.getClass()));
        field.setAccessible(oldAccessibilityValue);
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
}

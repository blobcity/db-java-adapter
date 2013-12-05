/**
 * Copyright 2011 - 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.search.SearchType;
import com.blobcity.db.search.SearchParams;
import com.blobcity.db.bquery.QueryExecuter;
import com.blobcity.db.classannotations.Entity;
import com.blobcity.db.constants.Credentials;
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.constants.QueryType;
import com.blobcity.db.exceptions.DbOperationException;
import com.blobcity.db.exceptions.InternalAdapterException;
import com.blobcity.db.exceptions.InternalDbException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides the connection and query execution framework for performing operations on the BlobCity data
 * store. This class must be extended by any Model that represents a BlobCity Entity.
 *
 * @author Sanket Sarang
 * @author Karishma
 * @version 1.0
 * @since 1.0
 */
public abstract class CloudStorage<T extends CloudStorage> {
    
    private String table = null;
    
    public CloudStorage() {
        for (Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation instanceof Entity) {
                Entity blobCityEntity = (Entity) annotation;
                table = blobCityEntity.table();
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
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
        }
    }
    
    public static <T extends CloudStorage> T newInstance(Class<T> clazz, Object pk) throws DbOperationException {
        try {
            T obj = clazz.newInstance();
            obj.setPk(pk);
            return obj;
        } catch (InstantiationException ex) {
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
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
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
        }
    }
    
    public static <T extends CloudStorage> List<Object> selectAll(Class<T> clazz) {
        JSONObject responseJson = postStaticRequest(clazz, QueryType.SELECT_ALL);
        JSONArray jsonArray;
        List<Object> list;
        
        try {
            if ("1".equals(responseJson.getString("ack"))) {
                jsonArray = responseJson.getJSONArray("keys");
                list = new ArrayList<Object>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.get(i));
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
    
    public static <T extends CloudStorage> List<Object> search(Class<T> clazz, SearchType searchType, SearchParams searchParams) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static <T extends CloudStorage> List<Object> filter(Class<T> clazz, String filterName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected void setPk(Object pk) throws DbOperationException {
        Field primaryKeyField = TableStore.getInstance().getPkField(table);
        try {
            primaryKeyField.setAccessible(true);
            primaryKeyField.set(this, pk);
            primaryKeyField.setAccessible(false);
        } catch (IllegalArgumentException ex) {
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
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
                } else {
                    reportIfError(responseJson);
                }
            }
        } catch (Exception ex) {
            reportIfError(responseJson);
        }
        
        return false;
    }
    
    public void remove() {
        JSONObject responseJson;
        responseJson = postRequest(QueryType.REMOVE);
        try {

            /* If ack:0 then check for error code and report accordingly */
            if ("0".equals(responseJson.getString("ack"))) {
                if (responseJson.getString("code").equals("DB200")) {
                    return;
                } else {
                    reportIfError(responseJson);
                }
            }
        } catch (JSONException ex) {
            throw new InternalDbException("Error in API JSON response", ex);
        }
    }
    
    public List<Object> searchOr() {
        throw new UnsupportedOperationException("Not yet supported.");
    }
    
    public List<String> searchAnd() {
        throw new UnsupportedOperationException("Not yet supported.");
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
                    requestJson.put("p", asJson());
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
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
        }
    }
    
    private static <T extends CloudStorage> JSONObject postStaticRequest(Class<T> clazz, QueryType queryType) {
        JSONObject requestJson;
        JSONObject responseJson;
        Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        if (entity == null) {
            throw new InternalDbException(clazz.getName() + " is not a valid entity class");
        }
        
        requestJson = new JSONObject();
        try {
            requestJson.put("app", Credentials.getInstance().getAppId());
            requestJson.put("key", Credentials.getInstance().getAppKey());
            requestJson.put("t", entity.table());
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
        Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        if (entity == null) {
            throw new InternalAdapterException(clazz.getName() + " is not a valid entity class");
        }
        
        requestJson = new JSONObject();
        try {
            requestJson.put("app", Credentials.getInstance().getAppId());
            requestJson.put("key", Credentials.getInstance().getAppKey());
            requestJson.put("t", entity.table());
            requestJson.put("q", queryType.getQueryCode());
            requestJson.put("pk", pk);
            
            final String responseString = new QueryExecuter().executeQuery(requestJson);
            responseJson = new JSONObject(responseString);
            return responseJson;
        } catch (JSONException ex) {
            throw new InternalDbException("Error in processing request/response JSON", ex);
        }
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
     * Gets a JSON representation of the object. The column names are same as those loaded in {@link TableStore}
     *
     * @return {@link JSONObject} representing the entity class in its current state
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws JSONException
     */
    private JSONObject asJson() throws IllegalArgumentException, IllegalAccessException, JSONException {
        JSONObject jsonObject = new JSONObject();
        
        Map<String, Field> structureMap = TableStore.getInstance().getStructure(table);
        
        for (String columnName : structureMap.keySet()) {
            Field field = structureMap.get(columnName);
            field.setAccessible(true);
            jsonObject.put(columnName, field.get(this));
        }
        
        return jsonObject;
    }
    
    private void fromJson(JSONObject jsonObject) {
        Map<String, Field> structureMap = TableStore.getInstance().getStructure(table);
        
        for (String columnName : structureMap.keySet()) {
            Field field = structureMap.get(columnName);
            field.setAccessible(true);
            try {
                field.set(this, jsonObject.get(columnName));
            } catch (JSONException ex) {
                throw new InternalDbException("Error in processing JSON", ex);
            } catch (IllegalArgumentException ex) {
                throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
            } catch (IllegalAccessException ex) {
                throw new InternalAdapterException("This exception is thrown when an error occurs that is internal to the adapter's operation", ex);
            }
        }
    }

    /**
     *
     * @param field
     * @param value
     * @throws IllegalAccessException
     */
    private void setFieldValue(Field field, Object value) throws IllegalAccessException {
        
        try {
            PropertyDescriptor p = new PropertyDescriptor(field.getName(), this.getClass());

            /* Check if the field to be set is of type ENUM */
            if (p.getPropertyType().isEnum()) {
                String str = p.getPropertyType().getName();
                try {
                    Class c = Class.forName(str);
                    Object[] enums = c.getEnumConstants();
                    for (Object o : enums) {
                        if (o.toString().equalsIgnoreCase(value.toString())) {
                            field.setAccessible(true);
                            field.set(this, o);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(CloudStorage.class.getName()).log(Level.SEVERE, null, ex + "-Class not found: " + str);
                }
            } /* Check of the value to be set is in the form of a JSONArray */ else if (value instanceof JSONArray) {
                
                JSONArray arr = (JSONArray) value;
                ArrayList l = new ArrayList();
                try {
                    for (int i = 0; i < arr.length(); i++) {
                        l.add(arr.get(i));
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(CloudStorage.class.getName()).log(Level.SEVERE, null, ex + "-" + field.getName());
                }
                p.getWriteMethod().invoke(this, l);
                
            } else if (field.getType() == List.class && "".equals(value)) {
                // Since the type required is List and the data is empty, value was an empty String a new ArrayList is to be given
                p.getWriteMethod().invoke(this, new ArrayList());
            } else {
                p.getWriteMethod().invoke(this, value);
            }
        } catch (Exception ex) {
            Logger.getLogger(CloudStorage.class.getName()).log(Level.SEVERE, "{0} couldn''t be set. Field Type was {1} but got {2}", new Object[]{field.getName(), field.getType(), value.getClass().getCanonicalName()});
        }
    }
}
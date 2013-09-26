package com.blobcity.db.iconnector;

import com.blobcity.db.bquery.QueryExecuter;
import com.blobcity.db.classannotations.BlobCityEntity;
import com.blobcity.db.constants.AutoDefineType;
import com.blobcity.db.constants.BlobCityConnectionMode;
import com.blobcity.db.constants.Credentials;
import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.constants.QueryType;
import com.blobcity.db.exceptions.DbOperationException;
import com.blobcity.db.exceptions.ExceptionType;
import com.blobcity.db.fieldannotations.AutoDefine;
import com.blobcity.db.fieldannotations.Index;
import com.blobcity.db.fieldannotations.Unique;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.QueryEval;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides the connection and query execution framework for
 * performing operations on the BlobCity data store. This class must be extended
 * by any POJO that represents a BlobCity Entity.
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 * @author Karishma
 * @version 1.0
 * @since 1.0
 */
public abstract class BlobCityCloudStorage {

    private String table = null;

    public BlobCityCloudStorage() {
        for (Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation instanceof BlobCityEntity) {
                BlobCityEntity blobCityEntity = (BlobCityEntity) annotation;
                table = blobCityEntity.table();
                break;
            }
        }

        if (table == null) {
            table = this.getClass().getName();
        }
        
        TableStore.getInstance().registerClass(table, this.getClass());
    }

    public static boolean exists(Class clazz, String key) {
        return true;
    }

    public static Object newInstance(Class clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Object newInstance(Class clazz, Object pk) {
        try {
            BlobCityCloudStorage obj = (BlobCityCloudStorage) clazz.newInstance();
            obj.setPk(pk);
            try {
                obj.load();
            } catch (DbOperationException ex) {
                Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            return obj;
        } catch (InstantiationException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void setPk(Object pk) {
        Field primaryKeyField = TableStore.getInstance().getPkField(table);
        try {
            primaryKeyField.setAccessible(true);
            primaryKeyField.set(this, pk);
            primaryKeyField.setAccessible(false);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* Database queries */
    private boolean createTable() throws DbOperationException {
        return (Boolean) postRequest(QueryType.CREATE_TABLE);
    }

    public boolean tableExists() throws DbOperationException {
        return (Boolean) postRequest(QueryType.TABLE_EXISTS);
    }

    /**
     * Loads a record based on primary key specified
     *
     * @return boolean to notify if the load was successful
     * @throws InvalidCredentialsException
     * @throws InvalidEntityException
     * @throws InvalidFieldException
     */
    public boolean load() throws DbOperationException {
        return (Boolean) postRequest(QueryType.SELECT);
    }

    /**
     * Performs INSERT/update of the records
     *
     * @throws InvalidCredentialsException
     * @throws InvalidEntityException
     * @throws InvalidFieldException
     * @return success/failure (true/false)
     */
    public boolean save() throws DbOperationException {
        return (Boolean) postRequest(QueryType.SAVE);
    }

    /**
     *
     * @throws InvalidCredentialsException
     * @throws InvalidEntityException
     * @throws InvalidFieldException
     * @throws RecordExistsException
     */
    public void insert() throws DbOperationException {
        postRequest(QueryType.INSERT);
    }

    /**
     *
     * @return @throws
     */
    public List<String> searchOR() throws DbOperationException {
        return (List<String>) postRequest(QueryType.SEARCH_OR);
    }

    /**
     *
     * @return @throws
     */
    public List<String> searchAND() throws DbOperationException {
        return (List<String>) postRequest(QueryType.SEARCH_AND);
    }

    /**
     * Will remove the record from the database matching the given Primary Key
     *
     * @return true is remove is successful else false
     * @throws NoPrimaryKeySpecifiedException If no primary key specified
     */
    public boolean remove() throws DbOperationException {
        return (Boolean) postRequest(QueryType.DELETE);
    }

    public List<String> selectAll() throws DbOperationException {
        if (!tableExists()) {
            throw new DbOperationException(ExceptionType.TABLE_NOT_FOUND);
        }
        return (List<String>) postRequest(QueryType.SELECT_ALL);
    }

    private Object postRequest(QueryType queryType) throws DbOperationException {
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("ac", Credentials.getInstance().getAppId());
            requestJson.put("key", Credentials.getInstance().getAppKey());
            requestJson.put("db", "db");
            requestJson.put("t", table);
            requestJson.put("q", queryType.name().replaceAll("_", "-"));
            requestJson.put("p", asJson());
            return new QueryExecuter().executeQuery(requestJson);
        } catch (JSONException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @param queryType
     * @return
     * @throws InvalidCredentialsException
     * @throws InvalidEntityException
     * @throws InvalidFieldException
     */
    private Object postRequest1(QueryType queryType) throws DbOperationException {

        /* Declarations */
        String columnName;
        Object columnValue = null;

        JSONObject jsonRequestObject = new JSONObject();
        JSONObject jsonPayloadObject = new JSONObject();

        /* Fetch all field level annotations */
        Field[] fieldsList = this.getClass().getDeclaredFields();

        /* Set all the blobcity credential parameters to query */
        try {
            jsonRequestObject.put("ac", Credentials.getInstance().getAppId());
            jsonRequestObject.put("key", Credentials.getInstance().getAppKey());
            jsonRequestObject.put("db", "db");
            jsonRequestObject.put("t", table);

            /* Set the query type */
            switch (queryType) {
                case SELECT:
                    jsonRequestObject.put("q", "SELECT");
                    break;
                case UPDATE:
                    jsonRequestObject.put("q", "SAVE");
                    break;
                case INSERT:
                    //create table if not exists
                    jsonRequestObject.put("q", "INSERT");
                    break;
                case DELETE:
                    jsonRequestObject.put("q", "DELETE");
                    break;
                case CREATE_TABLE:
                    jsonRequestObject.put("q", "CREATE-TABLE");
                    break;
                case TABLE_EXISTS:
                    jsonRequestObject.put("q", "TABLE-EXISTS");
                    break;
                case SELECT_ALL:
                    jsonRequestObject.put("q", "SELECT-ALL");
                    break;
                case SAVE:
                    jsonRequestObject.put("q", "SAVE");
                    break;
                case SEARCH_AND:
                    jsonRequestObject.put("q", "SEARCH-AND");
                    break;
                case SEARCH_OR:
                    jsonRequestObject.put("q", "SEARCH-OR");
                    break;
            }

            /* Set the payloads */
            if (queryType == QueryType.CREATE_TABLE) {

                JSONObject jsonColumnConstraints;
                Annotation[] annotationList;

                /*Fetch all fields and process them */

                /* Primary array to hold all the Primary key or Composite Primary keys */
                JSONArray primaryArray = primaryArray = new JSONArray();

                for (Field field : fieldsList) {

                    /* Set default values for all constraints on a column*/
                    String auto_numbered_value = AutoDefineType.NONE.toString();   //default
                    boolean unique_value = false;   //default
                    boolean index_value = false;    //default

                    /* JSON Column constraints sets all the constraints on a valid column */
                    jsonColumnConstraints = new JSONObject();

                    /* Get all the annotations on the field to process further*/
                    annotationList = field.getAnnotations();


                    for (Annotation a : annotationList) {
                        if (a instanceof Primary) {

                            //TODO: Karishma Composite keys not yet supported
                            primaryArray.put(((Column) field.getAnnotation(Column.class)).name());
                        } else if (a instanceof AutoDefine) {

                            /* Auto-Define values can be NONE or UUID */
                            auto_numbered_value = ((AutoDefine) a).type().toString();
                        } else if (a instanceof Unique) {
                            unique_value = true;
                        } else if (a instanceof Index) {
                            index_value = true;
                        }
                    }

                    /* Add Primary or Composite Primary array into the payload*/
                    jsonPayloadObject.put("primary", primaryArray);

                    /* Attach the other constaints on the column into the payload */
                    jsonColumnConstraints.put("type", getFieldType(field));
                    jsonColumnConstraints.put("auto_numbered", auto_numbered_value);
                    jsonColumnConstraints.put("unique", unique_value);
                    jsonColumnConstraints.put("index", index_value);

                    jsonPayloadObject.put(getMappedColumnName(field), jsonColumnConstraints);
                }
                jsonRequestObject.put("p", jsonPayloadObject);

            } /*
             * No payload to be attached in case of already existing table or for a Select-All query
             */ else if ((queryType == QueryType.TABLE_EXISTS) || (queryType == QueryType.SELECT_ALL)) {
                // jsonRequestObject.put("p", new JSONObject(""));
            } else {

                /*Fetch all fields and process them */
                for (Field field : fieldsList) {

                    //TODO Karishma: throws an exception if field is invalid. Do not catch exception here.
                    validateField(field);
                    columnName = getMappedColumnName(field);

                    try {
                        columnValue = this.getFieldValue(field);


                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        throw new DbOperationException(ExceptionType.NAN);
                    } catch (IllegalAccessException ex) {
                        //TODO: Verify whether to display custom exception
                        throw new DbOperationException(ExceptionType.NAN);
                    }
                    jsonPayloadObject.put(columnName, columnValue); //specifying type maybe mandatory
                }
                jsonRequestObject.put("p", jsonPayloadObject);
            }

            return processRequest(queryType, jsonRequestObject);
        } catch (JSONException ex) {
        }
        return false;
    }

    /**
     * Gets the data type of the field
     *
     * @param field
     * @return
     */
    private String getFieldType(Field field) {

        if ((field.getType() == Integer.class) || (field.getType() == int.class)) {
            return "INT";
        } else if ((field.getType() == Float.class) || (field.getType() == float.class)) {
            return "FLOAT";
        } else if ((field.getType() == Long.class) || (field.getType() == long.class)) {
            return "LONG";
        } else if ((field.getType() == Double.class) || (field.getType() == double.class)) {
            return "DOUBLE";
        } else if ((field.getType() == Boolean.class) || (field.getType() == boolean.class)) {
            return "BIT";
        } else if (field.getType().isEnum()) {
            return "VARCHAR";
        } else if (field.getType() == String.class) {
            return "VARCHAR";
        } else if ((field.getType() == List.class) || (field.getType() == ArrayList.class)) {
            ParameterizedType pt = (ParameterizedType) field.getGenericType();
            if (pt.getActualTypeArguments()[0] == String.class) {
                return "LIST<VARCHAR>";
            } else if ((pt.getActualTypeArguments()[0] == int.class) || (pt.getActualTypeArguments()[0] == Integer.class)) {
                return "LIST<INT>";
            } else if ((pt.getActualTypeArguments()[0] == long.class) || (pt.getActualTypeArguments()[0] == Long.class)) {
                return "LIST<LONG>";
            } else if (pt.getActualTypeArguments()[0] == Object.class) {
                return "LIST<OBJECT>";
            }
        }
        return field.getType().getSimpleName();
    }

    /**
     * Gets a JSON representation of the object. The column names are same as
     * those loaded in {@link TableStore}
     *
     * @return {@link JSONObject} representing the entity class in its current
     * state
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

    private Object processRequest(QueryType queryType, JSONObject jsonRequestObject) throws DbOperationException {

        String blobCityPostRequest = jsonRequestObject.toString();
        ////System.out.println("Query = " + blobCityPostRequest);

        JSONObject jsonResponseObject;
        JSONObject jsonPayloadResponseObject;
        JSONArray arr;
        OutputStreamWriter wr = null;

        String responseString = "";

        try {

            /* Runtime retrieval of IP */
//            String urlString = "";
//            if (mode == BlobCityConnectionMode.SANDBOX) {
//                urlString = "http://db.blobcity.com/ServerIPs?mode=sandbox&type=db-web-endpoint";
//            } else if (mode == BlobCityConnectionMode.PRODUCTION) {
//                urlString = "http://db.blobcity.com/ServerIPs?mode=production&type=db-web-endpoint";
//            }
//            URL ipRequestURL = new URL(urlString);
//            URLConnection connection = ipRequestURL.openConnection();
//            BufferedReader ipreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String jsonString = ipreader.readLine();
//
//            /* JSON Object */
//            JSONObject obj = new JSONObject(jsonString);
//            JSONArray ipArray = obj.getJSONArray("ips");
//            String currentIP = ipArray.getString(0);

            String currentIP = "db2.blobcity.com:8080";

            //String currentIP = "123.238.35.180";
            //String currentIP = "182.72.4.203";
            //String currentIP = "localhost";

            // String currentIP = "115.112.185.99";
            /* Prepare URL Request*/
            String data = URLEncoder.encode(blobCityPostRequest, "UTF-8");
            //URL url = new URL("http://" + currentIP + "/BlobCityDbWeb/BQueryExecuter");
            URL url = new URL("http://" + currentIP + "/BQueryExecuter");

            /* Open URL connection */
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            /* Send request */
            ////System.out.println("====================Request to the db : " + blobCityPostRequest);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            /* Receive response */
            responseString = rd.readLine();
            ////System.out.println("===================Response from db : " + responseString);

            if (responseString.equals("null")) {
                throw new DbOperationException(ExceptionType.CONNECTION_ERROR, "Connection Lost");
            }
            /* Read the response into a JSON Object */
            jsonResponseObject = new JSONObject(responseString);


            /* Parse the response according to the Request Object */
            switch (queryType) {
                case SELECT:
                    if (jsonResponseObject.getInt("ack") == 0) {
                        if (jsonResponseObject.getString("cause") != null) {
                            throw new DbOperationException(ExceptionType.NAN, jsonResponseObject.getString("cause"));
                        }


                    } else if (jsonResponseObject.getInt("ack") == 1) {

                        //if the save is successful, and the payload is retrieved, set it back to the object
                        if (jsonResponseObject.has("p")) {
                            jsonPayloadResponseObject = jsonResponseObject.getJSONObject("p");
                            setResponse(jsonPayloadResponseObject);
                            return true;
                        } else {//TODO: no payload found
                            return false;

                        }

                    }
                    break;

                case UPDATE:

                    break;
                case INSERT:
                    if (jsonResponseObject.getInt("ack") == 1) {

                        /* ack=1 process payload to set back the data to the POJO*/
                        if (jsonResponseObject.has("p")) {
                            jsonPayloadResponseObject = jsonResponseObject.getJSONObject("p");
                            setResponse(jsonPayloadResponseObject);
                            return true;
                        }

                    } //create table if not exists
                    else if (jsonResponseObject.getInt("ack") == 0) {
                        ////System.out.println("Ack 0 with a response: " + jsonResponseObject.toString());

                        //TODO Karishma: Process further, throw exception
                        if (jsonResponseObject.getString("cause").equalsIgnoreCase("Unable to read table structure")) {
                            if ((Boolean) postRequest(QueryType.CREATE_TABLE)) {
                                postRequest(QueryType.INSERT);
                                return true;
                            }
                        } else if (jsonResponseObject.getString("cause").equalsIgnoreCase("A record with the given primary key already exists")) {
                            throw new DbOperationException(ExceptionType.CONNECTION_ERROR.RECORD_EXISTS, jsonResponseObject.getString("cause"));
                        }
                    }
                    break;
                case DELETE:
                    if (jsonResponseObject.getInt("ack") == 1) {
                        return true;
                    } else {
                        return false;
                    }
                case CREATE_TABLE:

                    break;
                case TABLE_EXISTS:

                    if (jsonResponseObject.getInt("ack") == 1) {
                        if (jsonResponseObject.getInt("exists") == 1) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    break;
                case SELECT_ALL:
                    if (jsonResponseObject.getInt("ack") == 1) {
                        if (jsonResponseObject.has("keys")) {
                            arr = jsonResponseObject.getJSONArray("keys");
                            List<String> primarykeyList = new ArrayList<String>();
                            for (int i = 0; i < arr.length(); i++) {
                                primarykeyList.add(arr.getString(i));
                            }
                            return primarykeyList;
                        }
                    }
                    break;
                case SAVE:

                    //if the save is successful, and the payload is retrieved, set it back to the object
                    if (jsonResponseObject.getInt("ack") == 1) {

                        if (jsonResponseObject.has("p")) {
                            jsonPayloadResponseObject = jsonResponseObject.getJSONObject("p");
                            setResponse(jsonPayloadResponseObject);

                        }
                        return true;
                    }
                    break;

                case SEARCH_AND:

                    break;
                case SEARCH_OR:

                    break;
            }

            /* Check for ack and payload fields */
            if (jsonResponseObject.getInt("ack") == 1) {

                if (jsonResponseObject.has("p")) {
                    jsonPayloadResponseObject = jsonResponseObject.getJSONObject("p");
                    setResponse(jsonPayloadResponseObject);

                } else if (jsonResponseObject.has("keys")) {
                    arr = jsonResponseObject.getJSONArray("keys");
                    List<String> primarykeyList = new ArrayList<String>();
                    for (int i = 0; i < arr.length(); i++) {
                        primarykeyList.add(arr.getString(i));
                    }
                    return primarykeyList;
                }

            } else {

                /* ack = 0 */
                ////System.out.println("Ack 0 with a response: " + jsonResponseObject.toString());
                try {
                    //TODO Karishma: Process further, throw exception
                    if (jsonResponseObject.getString("cause").equalsIgnoreCase("Unable to read table structure")) {
                        if ((Boolean) postRequest(QueryType.CREATE_TABLE)) {
                            postRequest(QueryType.INSERT);
                            return true;
                        }
                    } else if (jsonResponseObject.getString("cause").equalsIgnoreCase("A record with the given primary key already exists")) {
                        throw new DbOperationException(ExceptionType.RECORD_EXISTS, jsonResponseObject.getString("cause"));
                    }
                } catch (JSONException e) {
                }
                return false;
            }
            wr.close();
            rd.close();
        } catch (JSONException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
            throw new DbOperationException(ExceptionType.CONNECTION_ERROR, "Connection Failed");
        } catch (IOException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                wr.close();
            } catch (IOException ex) {
                Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     * Set response from the database back to the invoking POJO object
     *
     * @param jsonPayloadObject
     */
    private void setResponse(JSONObject jsonPayloadObject) {

        Map<String, Field> structureMap = TableStore.getInstance().getStructure(table);

        Field[] fieldList = this.getClass().getDeclaredFields();
        try {

            /* Set the field value */
            for (Field field : fieldList) {
                String columnName = getMappedColumnName(field);
                this.setFieldValue(field, jsonPayloadObject.get(columnName));
            }

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            ////System.out.println(ex);
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Validate all annotation fields
     *
     * @param field
     * @throws InvalidFieldException
     */
    @Deprecated
    private void validateField(Field field) throws DbOperationException {
//        //TODO Karishma: Implement this
//        for (Annotation annotaion : field.getAnnotations()) {
//            if (annotaion.annotationType().equals(Primary.class)) {
//
//                /* Check for primary not null */
//                Primary primary = (Primary) annotaion;
//
//                if (primary == null) {
//                    throw new DbOperationException(ExceptionType.NAN);
//                }
//            } else if (annotaion.annotationType().equals(Column.class)) {
//
//                /* Check for name not null */
//                Column col = (Column) annotaion;
//
//
//                if (col.name().isEmpty()) {
//                    throw new DbOperationException(ExceptionType.NAN);
//                }
//            }
//        }
    }

    @Deprecated
    private String getMappedColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column == null) {
            return field.getName();
        }
        return column.name();
    }

    /**
     * Gets the value associated with the field
     *
     * @param field
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private Object getFieldValue(Field field) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object value = "";
        try {

            /* Retrieve the "getter" associtated with the field */
            PropertyDescriptor p = new PropertyDescriptor(field.getName(), this.getClass());
            value = p.getReadMethod().invoke(this, null);


            //if(value.getClass().getAnnotations())
            /* Get the value associated with the field */
            value = value == null ? "" : value;

        } catch (IntrospectionException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
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
                    Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex + "-Class not found: " + str);
                }
            } /* Check of the value to be set is in the form of a JSONArray */ else if (value instanceof JSONArray) {

                JSONArray arr = (JSONArray) value;
                ArrayList l = new ArrayList();
                try {
                    for (int i = 0; i < arr.length(); i++) {
                        l.add(arr.get(i));
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex + "-" + field.getName());
                }
                p.getWriteMethod().invoke(this, l);

            } else if (field.getType() == List.class && "".equals(value)) {
                // Since the type required is List and the data is empty, value was an empty String a new ArrayList is to be given
                p.getWriteMethod().invoke(this, new ArrayList());
            } else {
                p.getWriteMethod().invoke(this, value);
            }
        } catch (Exception ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, "{0} couldn''t be set. Field Type was {1} but got {2}", new Object[]{field.getName(), field.getType(), value.getClass().getCanonicalName()});
        }
    }
}

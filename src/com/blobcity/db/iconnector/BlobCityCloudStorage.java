/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.iconnector;

import com.blobcity.db.classannotations.BlobCityCredentials;
import com.blobcity.db.classannotations.BlobCityEntity;
import com.blobcity.db.constants.AutoDefineType;
import com.blobcity.db.constants.BlobCityConnectionMode;
import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.CompositePrimaryItem;
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.constants.CustomAnnotations;
import com.blobcity.db.constants.JSONConstants;
import com.blobcity.db.constants.QueryType;
import com.blobcity.db.credentials.AppCredentials;
import com.blobcity.db.exceptions.InvalidCredentialsException;
import com.blobcity.db.exceptions.InvalidEntityException;
import com.blobcity.db.exceptions.InvalidColumnFormatException;
import com.blobcity.db.exceptions.InvalidFieldException;
import com.blobcity.db.exceptions.OperationFailed;
import com.blobcity.db.exceptions.RecordExistsException;
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
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides the connection and query execution framework for performing
 * operations on the BlobCity data store. This class must be extended by any POJO
 * that represents a BlobCity Entity.
 *
 * @author sanketsarang
 * @author Karishma
 * @version 1.0
 */
public abstract class BlobCityCloudStorage {

    private BlobCityEntity blobCityEntity = null;
    private String account = "";
    private String token = "";
    private String user = "";
    /* Set the connection type for connecting to the database */
    public static BlobCityConnectionMode mode = BlobCityConnectionMode.SANDBOX;

    /* Fetch all class level annotations defined for the extending class */
    private Annotation[] annotationsList = this.getClass().getAnnotations();

    /**
     * Constructor to set the BlobCity Credentials externally
     * @param account 
     * @param user 
     * @param token 
     */
    public BlobCityCloudStorage(String account, String user, String token) {
        this.account = account;
        this.token = token;
        this.user = user;
    }

    public BlobCityCloudStorage() {
        account = AppCredentials.getInstance().getAccount();
        user = AppCredentials.getInstance().getUser();
        token = AppCredentials.getInstance().getToken();
    }

    /**
     * Validates the extending class with all the annotation rules defined
     * @throws InvalidClassFormatException with proper cause.
     */
    private void validate() throws InvalidColumnFormatException {
        /* Declarations */

        /* HashMap to save the custom annotaions. It holds a list of valid annotation tpes
         * Required for validation.
         */
        Map map = new HashMap<String, Integer>();

        /* Get the columnlist declared in the class */
        Field[] columnList = this.getClass().getDeclaredFields();

        for (Field f : columnList) {

            /* Read all the associated annotations defined on a field */
            for (Annotation a : f.getAnnotations()) {

                /* Process each annotation. Update count for each annotation into the HashMap. */
                if (a instanceof Column) {

                    /* Validate column name to be as mentioned in the specifications */
                    //TODO Karishma Validate and throw exception of "Invalid Column name"
                    //System.out.println("Column " + ((Column) a).name() + " Matches? " + ((Column) a).name().matches("^[a-zA-Z][a-zA-Z0-9_]+$"));

                    updateHashtable(map, CustomAnnotations.COLUMN.toString());
                } else if (a instanceof Primary) {
                    updateHashtable(map, CustomAnnotations.PRIMARY.toString());
                } else if (a instanceof CompositePrimaryItem) {
                    updateHashtable(map, CustomAnnotations.COMPOSITEPRIMARYITEM.toString());
                }
            }
        }
        processAnnotations(map, columnList);
    }

    /**
     * Process field annotations to validate the extending class
     * @param map is a map of valid Custom annotations
     * @param fieldList is a list of Fields in the POJO
     * @throws InvalidColumnFormatException If all annotation rules are not met
     *
     * TODO BC: Define Annotation rules for JAVADOCS
     */
    private void processAnnotations(Map map, Field[] fieldList) throws InvalidColumnFormatException {

        if ( //Case : No Columns defined
                (!map.containsKey(CustomAnnotations.COLUMN.toString()))
                || //Case : Every field must have @Column annotation
                (((Integer) map.get(CustomAnnotations.COLUMN.toString())) != fieldList.length)
                || //Case : Primary key absent and no Composite fields defined
                (!map.containsKey(CustomAnnotations.PRIMARY) && (((Integer) map.get(CustomAnnotations.COMPOSITEPRIMARYITEM.toString())) < 2))
                || //Case : Has Primary and Composite fields defined
                (map.containsKey(CustomAnnotations.PRIMARY) && (((Integer) map.get(CustomAnnotations.COMPOSITEPRIMARYITEM.toString())) > 2))
                || //Case : Primary and Composite fields not defined
                (!map.containsKey(CustomAnnotations.PRIMARY) && !(((Integer) map.get(CustomAnnotations.COMPOSITEPRIMARYITEM.toString())) < 2))) {
            throw new InvalidColumnFormatException();
        }
    }

    /**
     * Holds the number of
     * @param map Holds the custom annotations list
     * @param key Number of occurances of a custom annotation in the POJO
     */
    private void updateHashtable(Map map, String key) {
        int value = (Integer) map.get(key) == null ? 0 : (Integer) map.get(key);
        map.put(key, ++value);
    }

    /* Database queries */
    private boolean createTable() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed {
        try {
            return (Boolean) postRequest(QueryType.CREATE_TABLE);
        } catch (RecordExistsException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean tableExists() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed {
        try {
            return (Boolean) postRequest(QueryType.TABLE_EXISTS);
        } catch (RecordExistsException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Loads a record based on primary key specified
     * @return boolean to notify if the load was successful
     * @throws InvalidCredentialsException
     * @throws InvalidEntityException
     * @throws InvalidFieldException
     */
    public boolean load() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed {
        try {

//            if (!tableExists()) {
//                throw new OperationFailed("Table does not exist");
//            }
            return (Boolean) postRequest(QueryType.SELECT);
        } catch (RecordExistsException ex) {
            //Do Nothing
        }
        return false;
    }

    /**
     * Performs INSERT/update of the records
     *
     * @throws InvalidCredentialsException
     * @throws InvalidEntityException
     * @throws InvalidFieldException
     * @return success/failure (true/false)
     */
    public boolean save() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed {
        try {
            return (Boolean) postRequest(QueryType.SAVE);
        } catch (RecordExistsException ex) {
            //Do Nothing
        }
        return false;
    }

    private boolean update() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed {
        try {
            return (Boolean) postRequest(QueryType.UPDATE);
        } catch (RecordExistsException ex) {
            //Do Nothing
        }
        return false;
    }

    /**
     *
     * @throws InvalidCredentialsException
     * @throws InvalidEntityException
     * @throws InvalidFieldException
     * @throws RecordExistsException
     */
    public void insert() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, RecordExistsException, OperationFailed {
        postRequest(QueryType.INSERT);
    }

    /**
     * 
     * @return 
     * @throws 
     */
    public List<String> searchOR() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed {
        try {
            return (List<String>) postRequest(QueryType.SEARCH_OR);
        } catch (RecordExistsException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * 
     * @return 
     * @throws 
     */
    public List<String> searchAND() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed {
        try {
            return (List<String>) postRequest(QueryType.SEARCH_AND);
        } catch (RecordExistsException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Will remove the record from the database matching the given Primary Key
     * @return true is remove is successful else false
     * @throws NoPrimaryKeySpecifiedException If no primary key specified
     */
    public boolean remove() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed {
        try {
            return (Boolean) postRequest(QueryType.DELETE);
        } catch (RecordExistsException ex) {
            //Do nothing
        }
        return false;
    }

    public List<String> selectAll() throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed {
        try {
            if (!tableExists()) {
                throw new OperationFailed("Table does not exist");
            }
            return (List<String>) postRequest(QueryType.SELECT_ALL);


        } catch (RecordExistsException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * TODO Karishma : Throw all relevant exceptions.
     *
     * @param queryType
     * @return
     * @throws InvalidCredentialsException
     * @throws InvalidEntityException
     * @throws InvalidFieldException
     */
    private Object postRequest(QueryType queryType) throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed, RecordExistsException {

        /* Declarations */
        String columnName;
        Object columnValue = null;

        JSONObject jsonRequestObject = new JSONObject();
        JSONObject jsonPayloadObject = new JSONObject();

        /* Fetch all field level annotations */
        Field[] fieldsList = this.getClass().getDeclaredFields();

        /* Fetch the credentials for preparing query */


        if (annotationsList.length == 0) {
            throw new OperationFailed("No Credentials found");
        }
        for (Annotation annotation : annotationsList) {
            if (annotation.annotationType().equals(BlobCityCredentials.class)) {
                if (account.isEmpty() || (token.isEmpty()) || (user.isEmpty())) {
                    account = ((BlobCityCredentials) annotation).account();
                    token = ((BlobCityCredentials) annotation).token();
                    user = ((BlobCityCredentials) annotation).user();
                    validateCredentials((BlobCityCredentials) annotation);
                }
            } else if (annotation.annotationType().equals(BlobCityEntity.class)) {
                blobCityEntity = (BlobCityEntity) annotation;
                validateEntity(blobCityEntity);
            }
        }
//        try {
//            /* Validate all fields */
//            validate();
//        } catch (InvalidColumnFormatException ex) {
//            throw new OperationFailed("Invalid Column Format");
//        }

        /* Set all the blobcity credential parameters to query */
        try {
            jsonRequestObject.put("ac", account);
            jsonRequestObject.put("db", blobCityEntity.db());
            jsonRequestObject.put("t", blobCityEntity.table());

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
                        throw new InvalidFieldException("Unknown Error");
                    } catch (IllegalAccessException ex) {
                        //TODO: Verify whether to display custom exception
                        throw new InvalidFieldException(ex.getMessage());
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

    private Object processRequest(QueryType queryType, JSONObject jsonRequestObject) throws InvalidCredentialsException, InvalidEntityException, InvalidFieldException, OperationFailed, RecordExistsException {

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
            System.out.println("====================Request to the db : " + blobCityPostRequest);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            /* Receive response */
            responseString = rd.readLine();
            System.out.println("===================Response from db : " + responseString);

            if (responseString.equals("null")) {
                throw new OperationFailed("Connection Lost");
            }
            /* Read the response into a JSON Object */
            jsonResponseObject = new JSONObject(responseString);


            /* Parse the response according to the Request Object */
            switch (queryType) {
                case SELECT:
                    if (jsonResponseObject.getInt("ack") == 0) {
                        if(jsonResponseObject.getString("cause") != null){
                            throw new OperationFailed(jsonResponseObject.getString("cause"));
                        }

                        
                    } else if (jsonResponseObject.getInt("ack") == 1) {

                        //if the save is successful, and the payload is retrieved, set it back to the object
                        if (jsonResponseObject.has("p")) {
                            jsonPayloadResponseObject = jsonResponseObject.getJSONObject("p");
                            setResponse(jsonPayloadResponseObject);
                            return true;
                        }
                        else{//TODO: no payload found
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
                            throw new RecordExistsException(jsonResponseObject.getString("cause"));
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
                        throw new RecordExistsException(jsonResponseObject.getString("cause"));
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
            throw new OperationFailed("Connection Failed");
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
     * @param jsonPayloadObject
     */
    private void setResponse(JSONObject jsonPayloadObject) {
        ////System.out.println("Response: " + jsonPayloadObject);

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
        } catch (InvalidFieldException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            ////System.out.println(ex);
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Validate the columns to have valid non-null blobcity credentials
     * @param blobCityCredentials
     * @throws InvalidCredentialsException
     */
    private void validateCredentials(BlobCityCredentials blobCityCredentials) throws InvalidCredentialsException {
        try {
            if (blobCityCredentials.account().isEmpty()
                    || blobCityCredentials.user().isEmpty()
                    || blobCityCredentials.token().isEmpty()) {
                throw new InvalidCredentialsException("Not all credentials are specified");
            }
            //TODO: Perform other validations here if necessary
        } catch (NullPointerException ex) {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Validate the blobcity entity to have valid database and table fields
     * @param blobCityEntity
     * @throws InvalidEntityException
     */
    private void validateEntity(BlobCityEntity blobCityEntity) throws InvalidEntityException {
        try {
            if (blobCityEntity.db().isEmpty() || blobCityEntity.table().isEmpty()) {
                throw new InvalidEntityException("Not all items of entity specified");
            }
            //TODO: Perform other validations here if necessary
        } catch (NullPointerException ex) {
            throw new InvalidEntityException();
        }
    }

    /**
     * Validate all annotation fields
     * @param field
     * @throws InvalidFieldException
     */
    private void validateField(Field field) throws InvalidFieldException {

        //TODO Karishma: Implement this
        for (Annotation annotaion : field.getAnnotations()) {
            if (annotaion.annotationType().equals(Primary.class)) {

                /* Check for primary not null */
                Primary primary = (Primary) annotaion;

                if (primary
                        == null) {
                    throw new InvalidFieldException("No Primary key defined");
                }
            } else if (annotaion.annotationType().equals(Column.class)) {

                /* Check for name not null */
                Column col = (Column) annotaion;


                if (col.name().isEmpty()) {
                    throw new InvalidFieldException("Column name not specified");
                }
            }
        }
    }

    private String getMappedColumnName(Field field) throws InvalidFieldException {
        Column column = field.getAnnotation(Column.class);
        if (column == null) {
            throw new InvalidFieldException("Column mapping not specified");
        }
        return column.name();
    }

    /**
     * TODO BC: Yet to implement
     * @param pk
     * @param e
     * @return
     */
    protected static boolean contains(Object pk, Class e) {
        Class class1 = e;
        ////System.out.println("Class is : " + e + "\nAnnotations: " + class1.getDeclaredAnnotations().length);
        throw new UnsupportedOperationException("Not yet supported");
    }

    /**
     * Gets the value associated with the field
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
                    Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex +"-Class not found: "+str);
                }
            } /* Check of the value to be set is in the form of a JSONArray */ else if (value instanceof JSONArray) {

                JSONArray arr = (JSONArray) value;
                ArrayList l = new ArrayList();
                try {
                    for (int i = 0; i < arr.length(); i++) {
                        l.add(arr.get(i));
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex+"-"+field.getName());
                }
                p.getWriteMethod().invoke(this, l);

            } else if (field.getType() == List.class && "".equals(value)) {
                // Since the type required is List and the data is empty, value was an empty String a new ArrayList is to be given
                p.getWriteMethod().invoke(this, new ArrayList());
            }
            else {
                p.getWriteMethod().invoke(this, value);
            }
        } catch (Exception ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, "{0} couldn''t be set. Field Type was {1} but got {2}", new Object[]{field.getName(), field.getType(), value.getClass().getCanonicalName()});
        } 
    }

    /**
     * @return JSON Object
     */
    public JSONObject toJSON() {
        return null;
    }

    /**
     *
     * @return String equivalent of the JSON Object for the POJO
     */
    public String toJSONString() {
        return "";
    }

    //TODO: Karishma  - Testing purpose only
    private void processDummyresponse() {
        try {

            // {"ack":"1","p":{"column1":"0","column2":"2"}}

            //{"db":"db1","t":"table1","q":"SELECT","p":{"column1":"abc","column2":"XYZ-3"},"ac":"abcom"}
            String request = "{\"db\":\"db1\",\"t\":\"table1\",\"q\":\"select\",\"p\":{\"column1\":\"abc\",\"column2\":\"XYZ-3\"},\"ac\":\"abcom\"}";

            BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String responseStr = rd.readLine();
                ////System.out.println("Response from database ========= " + responseStr);

                //process the response string
                JSONObject jobject = new JSONObject(responseStr);
                if (jobject.getInt(JSONConstants.ACK) == 1) {
                    if (!jobject.getString(JSONConstants.PAYLOAD).isEmpty()) {
                    }
                }
                if (jobject.getInt(JSONConstants.ACK) == 1) {
                    if (jobject.getString(responseStr) == "") {
                        //throw a custom exception for no payload found
                    }
                }
                //System.out.println("Arr[0] : " + jobject.getString(JSONConstants.ACK));
                //System.out.println("Arr[1] : " + jobject.getString(JSONConstants.PAYLOAD));
            }
        } catch (JSONException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //TODO: Karishma  - Testing purpose only
    private String queryBuilderTest() {
        String blobCityPostRequest = "";
        String response = "";
        try {
            // Construct data
            blobCityPostRequest = "{\"db\":\"db1\",\"t\":\"table1\",\"q\":\"select\",\"p\":{\"column1\":\"abc\",\"column2\":\"XYZ-3\"},\"ac\":\"abcom\"}";

            //System.out.println("Query = " + blobCityPostRequest);
            String data = URLEncoder.encode(blobCityPostRequest, "UTF-8");
            // Send data
            //System.out.println("Query = " + data);
            URL url = new URL("http://10.241.199.198:8080/BlobCityDbWeb/BQueryExecuter");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response : {"ack":"1","p":{"column1":"0","column2":"2"}}
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((response = rd.readLine()) != null) {
                //System.out.println("Response from database ========= " + response);
            }
            wr.close();
            rd.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BlobCityCloudStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
}

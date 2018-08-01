/**
 * Copyright 2011 - 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.enums.CollectionType;
import com.blobcity.db.search.SearchParam;
import com.blobcity.db.annotations.Entity;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.annotations.Primary;
import com.blobcity.db.enums.AutoDefineType;
import com.blobcity.db.enums.ColumnType;
import com.blobcity.db.enums.IndexType;
import com.blobcity.db.enums.ReplicationType;
import com.blobcity.db.exceptions.DbOperationException;
import com.blobcity.db.exceptions.InternalAdapterException;
import com.blobcity.db.exceptions.InternalDbException;
import com.blobcity.db.search.Query;
import com.blobcity.db.search.StringUtil;
import com.google.gson.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class provides the connection and query execution framework for performing operations on the BlobCity data
 * store. This class must be extended by any Model that represents a BlobCity Entity.
 *
 * @author Sanket Sarang
 * @author Karishma
 * @author Karun AB
 * @version 1.0
 * @since 1.0
 */
public abstract class Db {

    private String collection = null;
    private String ds = null;
    protected String _id = null; //primary key value holder, in case the sub class does not have this property

    public Db() {
        for (Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation instanceof Entity) {
                final Entity blobCityEntity = (Entity) annotation;
                collection = blobCityEntity.collection();
                // if no ds is present in the entity
                if (StringUtil.isEmpty(blobCityEntity.ds())) {
                    ds = Credentials.getInstance().getDb();
                }
                else{
                    ds = blobCityEntity.ds();
                }

                if (StringUtil.isEmpty(collection)) {
                    collection = this.getClass().getSimpleName();
                }
                break;
            }
        }

        if (collection == null) {
            collection = this.getClass().getSimpleName();
        }
        if( ds ==null || ds.isEmpty() ){
            throw new InternalAdapterException("No datastore information found. Did you make a call to Credentials.init() ");
        }
        
        CollectionStore.getInstance().registerClass(ds, collection, this.getClass());
    }

    /**
     * Statically provides the ds name for any instance/child of {@link Db} that is internally used by the
     * adapter for querying. Note, this method is used by the adapter internally for SQL queries and the logic here
     * should be kept in sync with the rest of the class to ensure ds names are evaluated appropriately. This method can
     * be used for logging purposes where the ds name for a class is required.
     *
     * @param <T> Any class reference which extends {@link Db}
     * @param clazz class reference who's ds name is required
     * @return Name of the DB
     */
    public static <T extends Db> String getDs(final Class<T> clazz) {
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        return entity != null && !StringUtil.isEmpty(entity.ds()) ? entity.ds() : Credentials.getInstance().getDb();
    }
    
    public static String getDs() {
        return Credentials.getInstance().getDb();
    }
    
    /**
     * Statically provides the collection name for any instance/child of {@link Db} that is internally used by the
     * adapter for querying. Note, this method is not used by the adapter internally but the logic here, should be kept
     * in sync with the rest of the class to ensure collection names are evaluated appropriately. This method can be used for
     * logging purposes where the collection name for a class is required.
     *
     * @param <T> Any class reference which extends {@link Db}
     * @param clazz class reference who's collection name is required
     * @return Name of the collection
     */
    public static <T extends Db> String getCollection(final Class<T> clazz) {
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        return entity != null && !StringUtil.isEmpty(entity.collection()) ? entity.collection() : clazz.getSimpleName();
    }

    public void set_id(final String _id) {
        final Field primaryKeyField = CollectionStore.getInstance().getStructure(ds, collection).get("_id");
        if (primaryKeyField == null) {
            throw new InternalAdapterException("Call to set_id failed as could not find field _id in" + collection + " [" + this.getClass().getName() + "]");
        }
        synchronized (primaryKeyField) {
            final boolean accessible = primaryKeyField.isAccessible();

            try {
                if (!accessible) {
                    primaryKeyField.setAccessible(true);
                }

                primaryKeyField.set(this, _id);
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
    }

    public String get_id() {
        final Field primaryKeyField = CollectionStore.getInstance().getStructure(ds, collection).get("_id");
        if (primaryKeyField == null) {
            throw new InternalAdapterException("Call to get_id failed as could not find field _id in" + collection + " [" + this.getClass().getName() + "]");
        }
        synchronized (primaryKeyField) {
            final boolean accessible = primaryKeyField.isAccessible();

            try {
                if (!accessible) {
                    primaryKeyField.setAccessible(true);
                }

                return primaryKeyField.get(this).toString();
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
    
    protected void setPk(Object pk) {
        final Field primaryKeyField = CollectionStore.getInstance().getPkField(ds, collection);
        if (primaryKeyField == null) {
            throw new InternalAdapterException("Missing mandatory @Primary annotation for entity " + collection + " [" + this.getClass().getName() + "]");
        }
        synchronized (primaryKeyField) {
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
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static <T extends Db> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }

    public static <T extends Db> T newInstance(Class<T> clazz, Object pk) {
        try {
            T obj = clazz.newInstance();
            obj.setPk(pk);
            return obj;
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }

    public static <T extends Db> T newLoadedInstance(Class<T> clazz, Object pk) {
        try {
            T obj = clazz.newInstance();
            obj.setPk(pk);
            if (obj.load()) {
                return obj;
            }
            return null;
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }
    
    /**
     * Allows quick search queries on a single column. This method internally uses {@link #search(com.blobcity.db.search.Query)
     * }
     *
     * @see #search(com.blobcity.db.search.Query)
     * @param <T> Any class reference which extends {@link Db}
     * @param clazz class reference who's data is to be searched
     * @param columnName column to be searched
     * @param values values to be used to filter data in column
     * @return {@link List} of {@code T} that matches {@code searchParams}
     */
    public static <T extends Db> List<T> select(final Class<T> clazz, final String columnName, final Object... values) {
        return search(Query.table(clazz).where(SearchParam.create(columnName).in(values)));
    }

    public static <T extends Db> List<Object> selectAll(Class<T> clazz) {
        return selectAll(clazz, Object.class);
    }

    public static <T extends Db, K extends Object> List<K> selectAll(final Class<T> clazz, final Class<K> returnTypeClazz) {
        final DbQueryResponse response = postStaticRequest(Credentials.getInstance(), clazz, QueryType.SELECT_ALL);

        if (response.isSuccessful()) {
            return response.getKeys();
        }

        throw new DbOperationException(response.getErrorCode(), response.getErrorCause());
    }

    public static <T extends Db> boolean contains(final Class<T> clazz, final Object key) {
        return contains(Credentials.getInstance(), clazz, key);
    }

    public static <T extends Db> void remove(Class<T> clazz, final Object pk) {
        remove(Credentials.getInstance(), clazz, pk);
    }

    /**
     * Allows search queries to be performed as defined by
     * <a href="http://docs.blobcity.com/display/DB/Operations+on+data#Operationsondata-SEARCH">
     *     http://docs.blobcity.com/display/DB/Operations+on+data#Operationsondata-SEARCH</a>. This method internally
     * calls search(com.blobcity.ds.config.Credentials, com.blobcity.ds.search.Query) with default credentials
     * from {@link Credentials#getInstance()}
     *
     * Note: This return type is prone to update when support for multiple collection queries (joins) is introduced.
     *
     * see #search(com.blobcity.ds.config.Credentials, com.blobcity.ds.search.Query)
     * see Credentials#getInstance()
     * @param <T> Any class reference which extends {@link Db}
     * @param query {@link SearchParam}s which are to be used to search for data
     * @return {@link List} of {@code T} that matches {@code searchParams}
     */
    public static <T extends Db> List<T> search(final Query<T> query) {
        return search(Credentials.getInstance(), query);
    }

    public static <T extends Db> List<JSONObject> searchAsJson(final Query<T> query) {
        return searchAsJson(Credentials.getInstance(), query);
    }

    public static DbQueryResponse execute(final String sql) {
        return execute(Credentials.getInstance(), sql);
    }

    public static DbQueryResponse execute(final Credentials credentials, final String sql) {
        return QueryExecuter.executeSql(DbQueryRequest.create(credentials, sql));
    }
    
    public static <T extends Db> Object execute(final Query<T> query) {
        return execute(Credentials.getInstance(), query);
    }

    public static <T extends Db> Object execute(final Credentials credentials, final Query<T> query) {
        if (query.getFromTables() == null && query.getFromTables().isEmpty()) {
            throw new InternalAdapterException("No collection name set. Table name is a mandatory field queries.");
        }

        final String queryStr = query.asSql(credentials.getDb());

        final DbQueryResponse response = QueryExecuter.executeSql(DbQueryRequest.create(credentials, queryStr));

        final Class<T> clazz = (query.getFromTables() == null || query.getFromTables().isEmpty()) ? null : query.getFromTables().get(0);

        if (response.isSuccessful()) {

            //TODO: Throw away code
            if (response.getPayload() instanceof JsonArray) {
                final JsonArray resultJsonArray = response.getPayload().getAsJsonArray();
                final int resultCount = resultJsonArray.size();
                final List<T> responseList = new ArrayList<T>();
                final String tableName = clazz == null ? query.getFromTableStrings().get(0) : getCollection(clazz);
                final String dbName = clazz == null ? Db.getDs() : getDs(clazz);
                CollectionStore.getInstance().registerClass(dbName, tableName, clazz);
                final Map<String, Field> structureMap = CollectionStore.getInstance().getStructure(dbName, tableName);

                for (int i = 0; i < resultCount; i++) {
                    final T instance = Db.newInstance(clazz);
                    final JsonObject instanceData = resultJsonArray.get(i).getAsJsonObject();
                    final Set<Map.Entry<String, JsonElement>> entrySet = instanceData.entrySet();

                    for (final Map.Entry<String, JsonElement> entry : entrySet) {
                        final String columnName = entry.getKey();

                        final Field field = structureMap.get(columnName);
                        if(field == null) {
                            continue;
                        }
                        synchronized (field) {
                            final boolean oldAccessibilityValue = field.isAccessible();
                            field.setAccessible(true);

                            try {
                                field.set(instance, getCastedValue(field, instanceData.get(columnName), clazz));
                            } catch (IllegalArgumentException ex) {
                                throw new InternalAdapterException("Unable to set data into field \"" + clazz.getSimpleName() + "." + field.getName() + "\"", ex);
                            } catch (IllegalAccessException ex) {
                                throw new InternalAdapterException("Unable to set data into field \"" + clazz.getSimpleName() + "." + field.getName() + "\"", ex);
                            } finally {
                                field.setAccessible(oldAccessibilityValue);
                            }
                        }
                    }

                    responseList.add(instance);
                }
                return responseList;
            } else {
                JsonObject jsonObject = response.getPayload().getAsJsonObject();
                if (jsonObject.has("count")) {
                    return jsonObject.get("count").getAsLong();
                }
            }

        }

        throw new DbOperationException(response.getErrorCode(), response.getErrorCause());
    }

    public static void insertJson(final String collection, final JsonObject insertJson){
        insertJson(Credentials.getInstance(), collection, Arrays.asList(new JsonObject[]{insertJson}));
    }

    public static void insertJson(final Credentials credentials, final String collection, final JsonObject insertJson){
        insertJson(credentials, collection, Arrays.asList(new JsonObject[]{insertJson}));
    }

    public static void insertJson(final String collection, final List<JsonObject> jsonList) {
        insertJson(Credentials.getInstance(), collection, jsonList);
    }

    public static void insertJson(final Credentials credentials, final String collection, final List<JsonObject> jsonList) {
        if(collection == null || collection.isEmpty()) {
            throw new InternalAdapterException("collection name must be specified");
        }

        if(jsonList == null || jsonList.isEmpty()) {
            throw new InternalAdapterException("At-least one json required for a json insert operation");
        }

        JsonObject payloadJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        for(JsonObject element : jsonList) {
            dataArray.add(element);
        }
        payloadJsonObject.add(QueryConstants.DATA, dataArray);
        payloadJsonObject.addProperty(QueryConstants.TYPE, "json");

//        payloadJsonObject.addProperty("interpreter", "MyInterpreter");
//        payloadJsonObject.addProperty("interceptor", "MyInterceptor");

        final DbQueryResponse response = postStaticRequest(credentials, QueryType.INSERT, collection, payloadJsonObject);
        reportIfError(response);
    }

    public static void insertCsv(final String collection, final String csvString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insertCsv(final Credentials credentials, final String collection, final String csvString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insertCsv(final String collection, final List<String> csvList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insertCsv(final Credentials credentials, final String collection, final List<String> csvList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insertXml(final String collection, final String xmlString) {
        insertXml(Credentials.getInstance(), collection, Arrays.asList(new String[]{xmlString}));
    }

    public static void insertXml(final Credentials credentials, final String collection, final String xmlString) {
        insertXml(credentials, collection, Arrays.asList(new String[]{xmlString}));
    }

    public static void insertXml(final String collection, final List<String> xmlList) {
        insertXml(Credentials.getInstance(), collection, xmlList);
    }

    public static void insertXml(final Credentials credentials, final String collection, final List<String> xmlList) {
        if(collection == null || collection.isEmpty()) {
            throw new InternalAdapterException("collection name must be specified");
        }

        if(xmlList == null || xmlList.isEmpty()) {
            throw new InternalAdapterException("At-least one xml required for a xml insert operation");
        }

        JsonObject payloadJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        for(String element : xmlList) {
            dataArray.add(new JsonPrimitive(element));
        }
        payloadJsonObject.add(QueryConstants.DATA, dataArray);
        payloadJsonObject.addProperty(QueryConstants.TYPE, "xml");

        final DbQueryResponse response = postStaticRequest(credentials, QueryType.INSERT, collection, payloadJsonObject);
        reportIfError(response);
    }

    public static void insertSql(final String collection, final String sqlString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insertSql(final Credentials credentials, final String collection, final String sqlString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insertSql(final String collection, final List<String> sqlList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insertSql(final Credentials credentials, final String collection, final List<String> sqlList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insertText(final String collection, final String text) {
        insertText(Credentials.getInstance(), collection, Arrays.asList(new String[]{text}));
    }

    public static void insertText(final Credentials credentials, final String collection, final String text) {
        insertText(credentials, collection, Arrays.asList(new String[]{text}));
    }

    public static void insertText(final String collection, final List<String> textList) {
        insertText(Credentials.getInstance(), collection, textList);
    }

    public static void insertText(final Credentials credentials, final String collection, final List<String> textList) {
        insertText(credentials, collection, textList, null);
    }

    public static void insertText(final String collection, final String text, final String interpreterName) {
        insertText(Credentials.getInstance(), collection, Arrays.asList(new String[]{text}), interpreterName);
    }

    public static void insertText(final String collection, final List<String> text, final String interpreterName) {
        insertText(Credentials.getInstance(), collection, text, interpreterName);
    }

    public static void insertText(final Credentials credentials, final String collection, final String text, final String interpreterName) {
        insertText(credentials, collection, Arrays.asList(new String[]{text}), interpreterName);
    }

    public static void insertText(final Credentials credentials, final String collection, final List<String> textList, final String interpreterName) {
        if(collection == null || collection.isEmpty()) {
            throw new InternalAdapterException("collection name must be specified");
        }

        if(textList == null || textList.isEmpty()) {
            throw new InternalAdapterException("At-least one text record required for a text insert operation");
        }

        JsonObject payloadJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        for(String element : textList) {
            dataArray.add(new JsonPrimitive(element));
        }
        payloadJsonObject.add(QueryConstants.DATA, dataArray);
        payloadJsonObject.addProperty(QueryConstants.TYPE, "text");

        if(interpreterName != null) {
            payloadJsonObject.addProperty(QueryConstants.INTERPRETER, interpreterName);
        }

        final DbQueryResponse response = postStaticRequest(credentials, QueryType.INSERT, collection, payloadJsonObject);
        reportIfError(response);
    }

    public static void insert(final String collection, final String insertString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insert(final Credentials credentials, final String collection, final String insertString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insert(final String collection, final List<String> insertList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void insert(final Credentials credentials, final List<String> insertList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //TODO: Add support for inserting other data formats

    /* Update queries */


    /**
     * Creates a datastore with the specified name
     * @param ds the datastore name
     * @return true if a new datastore is created; false otherwise
     */
    public static boolean createDs(final String ds) {
        return createDs(Credentials.getInstance(), ds);
    }

    /**
     * Creates a datastore with the specified name, buy using the specified connection credentials
     * @param credentials the credentials used to connect to the database
     * @param ds the name of the datastore
     * @return true if a new datastore is created; false otherwise
     */
    public static boolean createDs(final Credentials credentials, final String ds) {
        if(ds == null || ds.isEmpty()) {
            throw new InternalAdapterException("ds (datastore) name must be specified");
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", ds);
        DbQueryResponse response = postStaticRequest(credentials, QueryType.CREATE_DS, jsonObject);
        return response.getAckCode() == 1;
    }

    /**
     * Fetches the list of datastores accessible to the connecting user
     * @return the list of datastores if the operation is successful; {@link InternalAdapterException} otherwise
     */
    public static List<String> listDs() {
        return listDs(Credentials.getInstance());
    }

    /**
     * Fetches the list of datastores accessible to the connecting user, but connecting to the database using the
     * specified connection credentials
     * @param credentials the credentials used to connect to the datastore
     * @return the list of datastores if the operation is successful; {@link InternalAdapterException} otherwise
     */
    public static List<String> listDs(Credentials credentials) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        DbQueryResponse response = postStaticRequest(credentials, QueryType.LIST_DS, null);
        if(response.getAckCode() != 1) {
            throw new InternalAdapterException(response.getErrorCode() + " : " + response.getErrorCause());
        }
        JsonArray dsJsonArray = response.getPayload().getAsJsonObject().getAsJsonArray("ds");
        List<String> dsList = new ArrayList<String>();
        for(int i = 0; i < dsJsonArray.size(); i++) {
            dsList.add(dsJsonArray.get(i).getAsString());
        }

        return dsList;
    }

    /**
     * Checks if the specified datastore is present
     * @param ds name of the datastore
     * @return <code>true</code> if the datastore is present; <code>false</code> if the datastore is absent or not
     * accessible to the connecting user
     */
    public static boolean dsExists(final String ds) {
        return dsExists(Credentials.getInstance(), ds);
    }

    /**
     * Checks if the specified datastore is present, by connecting to the database using the specifeid credentials
     * @param credentials the credentails used to connect to the database
     * @param ds name of the datastore
     * @return <code>true</code> if the datastore is present; <code>false</code> if the datastore is absent or not
     * accessible to the connecting user
     */
    public static boolean dsExists(final Credentials credentials, final String ds) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(ds == null || ds.isEmpty()) {
            throw new InternalAdapterException("ds (datastore) name must be specified");
        }

        JsonObject requestPayload = new JsonObject();
        requestPayload.addProperty("ds", ds);
        DbQueryResponse response = postStaticRequest(credentials, QueryType.DS_EXISTS, requestPayload);

        if(response.getAckCode() != 1) {
            throw new InternalAdapterException(response.getErrorCode() + " : " + response.getErrorCause());
        }

        JsonObject responsePayload = response.getPayload().getAsJsonObject();
        return responsePayload.get("exists").getAsBoolean();
    }

    public static boolean truncateDs(final String ds) {
        return truncateDs(Credentials.getInstance(), ds);
    }

    public static boolean truncateDs(final Credentials credentials, final String ds) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(ds == null || ds.isEmpty()) {
            throw new InternalAdapterException("ds (datastore) name must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", ds);
        DbQueryResponse response = postStaticRequest(credentials, QueryType.TRUNCATE_DS, payloadJson);
        return response.getAckCode() == 1;
    }

    /**
     * Drops a datastore with the spcified name
     * @param ds the name of the datastore to drop
     * @return true if post operation a datastore with the given name is does not exist; false otherwise
     */
    public static boolean dropDs(final String ds) {
        return dropDs(Credentials.getInstance(), ds);
    }

    /**
     * Drops a datastore with the specified name; by connecting to the database using the specified credentials
     * @param credentials the credentials used to connect to the database
     * @param ds the name of the datastore
     * @return true if post operation a datastore with the given name does not exist; false otherwise
     */
    public static boolean dropDs(final Credentials credentials, final String ds) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(ds == null || ds.isEmpty()){
            throw new InternalAdapterException("ds (datastore) name must be specified");
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", ds);
        DbQueryResponse response = postStaticRequest(credentials, QueryType.DROP_DATASTORE, jsonObject);
        return response.getAckCode() == 1;
    }

    /**
     * Creates a new collection with the specified name and storage type
     * @param collection the name of the collection
     * @param collectionType the {@link CollectionType} specifying the type of storage for the collection
     * @return true if the new collection is created; false otherwise
     */
    public static boolean createCollection(final String collection, final CollectionType collectionType) {
        return createCollection(Credentials.getInstance(), collection, collectionType);
    }

    /**
     * Creates a new collection with the specified name and storage type, by connecting to the database with the
     * explicitly provided credentials
     * @param credentials the credentials to use for connecting to DB
     * @param collection the name of the collection
     * @param collectionType the {@link CollectionType} specifying the type of storage for the collection
     * @return true if the new collection is created; false otherwise
     */
    public static boolean createCollection(final Credentials credentials, final String collection, final CollectionType collectionType) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()){
            throw new InternalAdapterException("collection name must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", collection);
        payloadJson.addProperty("type", collectionType.getType());

        DbQueryResponse response = postStaticRequest(credentials, QueryType.CREATE_COLLECTION, payloadJson);
        return response.getAckCode() == 1;
    }

    /**
     * Creates a new collection with full parameter specifications
     * @param collection the name of the collection
     * @param collectionType the {@link CollectionType} specifying the type of storage for the collection
     * @param replicationType the {@link ReplicationType} specifying the data distribution strategy
     * @param replicationFactor specifies the number of replica copies to create. Valid only for
     *                          <code>ReplicationType.DISTRIBUTED</code> collections.
     *                          A value of 0 indicates no replication.
     * @return true if the new collection is created; false otherwise
     */
    public static boolean createCollection(final String collection, final CollectionType collectionType, final ReplicationType replicationType, final Integer replicationFactor){
        return createCollection(Credentials.getInstance(), collection, collectionType, replicationType, replicationFactor);
    }

    /**
     * Creates a new collection with full parameter specifications, by connecting to the database with the
     * explicitly provided credentials.
     * @param credentials the credentials to use for connecting to DB
     * @param collection the name of the collection
     * @param collectionType the {@link CollectionType} specifying the type of storage for the collection
     * @param replicationType the {@link ReplicationType} specifying the data distribution strategy
     * @param replicationFactor specifies the number of replica copies to create. Valid only for
     *                          <code>ReplicationType.DISTRIBUTED</code> collections.
     *                          A value of 0 indicates no replication.
     * @return true if the new collection is created; false otherwise
     */
    public static boolean createCollection(final Credentials credentials, final String collection, final CollectionType collectionType, final ReplicationType replicationType, final Integer replicationFactor){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty() ){
            throw new InternalAdapterException("collection name must be specified");
        }

        if(collectionType == null) {
            throw new InternalAdapterException("CollectionType must be specified");
        }

        if(replicationType == null){
            throw new InternalAdapterException("ReplicationType must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", collection);
        payloadJson.addProperty("type", collectionType.getType());
        payloadJson.addProperty("replication-type", replicationType.getType());
        payloadJson.addProperty("replication-factor", replicationFactor);

        DbQueryResponse response = postStaticRequest(credentials, QueryType.CREATE_COLLECTION, collection, payloadJson);
        return response.getAckCode() == 1;
    }

    /**
     * Fetches a list of the collections the connecting user is authorized to present within the specified datastore
     * @param ds the name of the datastore
     * @return the list of collections names as datastoreName.collectionName; {@link InternalAdapterException} in case
     * of any error
     */
    public static List<String> listCollections(final String ds) {
        return listCollections(Credentials.getInstance(), ds);
    }

    /**
     * Fetches a list of the collections the connecting user is authorized to present within the specified datastore, by
     * connecting to the database using the specified credentials
     * @param credentials the connection credentials
     * @param ds the name of the datastore
     * @return the list of collections names as datastoreName.collectionName; {@link InternalAdapterException} in case
     * of any error
     */
    public static List<String> listCollections(final Credentials credentials, final String ds) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(ds == null || ds.isEmpty()) {
            throw new InternalAdapterException("ds (datastore) name must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("ds", ds);
        DbQueryResponse response = postStaticRequest(credentials, QueryType.LIST_COLLECTIONS, payloadJson);
        if(response.getAckCode() != 1) {
            throw new InternalAdapterException(response.getErrorCode() + " : " + response.getErrorCause());
        }
        JsonArray collectionJsonArray = response.getPayload().getAsJsonObject().getAsJsonArray("c");
        List<String> collectionList = new ArrayList<String>();
        for(int i = 0; i < collectionJsonArray.size(); i++) {
            collectionList.add(collectionJsonArray.get(i).getAsString());
        }
        return collectionList;
    }

    /**
     * Checks if a collection is present within a specified datastore
     * @param ds name of datastore
     * @param collection name of collection
     * @return <code>true</code> if collection is present; <code>false</code> otherwise
     */
    public static boolean collectionExists(final String ds, final String collection) {
        return collectionExists(Credentials.getInstance(), ds, collection);
    }

    /**
     * Checks if a collection is present within a specified datastore by connecting to the database using specified
     * credentials
     * @param credentials the credentials used to connect to the database
     * @param ds name of the datastore
     * @param collection name of the collection
     * @return <code>true</code> if the collection is present; <code>false</code> otherwise
     */
    public static boolean collectionExists(final Credentials credentials, final String ds, final String collection) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(ds == null || ds.isEmpty()) {
            throw new InternalAdapterException("ds (datastore) name must be specified");
        }

        if(collection == null || collection.isEmpty()) {
            throw new InternalAdapterException("collection name must be specified");
        }

        JsonObject requestPayload = new JsonObject();
        requestPayload.addProperty("ds", ds);
        requestPayload.addProperty("c", collection);
        DbQueryResponse response = postStaticRequest(credentials, QueryType.COLLECTION_EXISTS, requestPayload);

        if(response.getAckCode() != 1) {
            throw new InternalAdapterException(response.getErrorCode() + " : " + response.getErrorCause());
        }

        JsonObject responsePayload = response.getPayload().getAsJsonObject();
        return responsePayload.get("exists").getAsBoolean();
    }

    public static boolean truncateCollection(final String collection){
        return truncateCollection(Credentials.getInstance(), collection);
    }

    public static boolean truncateCollection(final Credentials credentials, final String collection){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()) {
            throw new InternalAdapterException("collection name must be specified");
        }

        DbQueryResponse response = postStaticRequest(credentials, QueryType.TRUNCATE_COLLECTION, collection, null);
        return response != null;
    }
    
    public static boolean dropCollection(final String collection){
        return dropCollection(Credentials.getInstance(), collection);
    }

    public static boolean dropCollection(final Credentials credentials, final String collection){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()) {
            throw new InternalAdapterException("collection name must be specified");
        }

        DbQueryResponse response = postStaticRequest(credentials, QueryType.DROP_COLLECTION, collection, null);
        return response != null;
    }

    public static JSONObject fetchSchema(final Credentials credentials, final String collection) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()) {
            throw new InternalAdapterException("collection name must be specified");
        }

        DbQueryResponse response = postStaticRequest(credentials, QueryType.FETCH_SCHEMA, collection, null);

        if(response.getAckCode() != 1) {
            throw new InternalAdapterException(response.getErrorCode() + " : " + response.getErrorCause());
        }

        JsonObject responsePayload = response.getPayload().getAsJsonObject();
        return new JSONObject(responsePayload.toString());
    }

    public static JSONObject tableauRequiresSync() {
        return tableauRequiresSync(Credentials.getInstance());
    }

    public static JSONObject tableauRequiresSync(final Credentials credentials) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        DbQueryResponse response = postStaticRequest(credentials, QueryType.TABLEAU_REQUIRES_SYNC, null);

        if(response.getAckCode() != 1) {
            throw new InternalAdapterException(response.getErrorCode() + " : " + response.getErrorCause());
        }

        JsonObject responsePayload = response.getPayload().getAsJsonObject();
        return new JSONObject(responsePayload.toString());
    }
    
    public static boolean addColumn(final String collection, final String columnName, final ColumnType columnType, final IndexType indexType, final AutoDefineType autoDefineType){
        return addColumn(Credentials.getInstance(), collection, columnName, columnType, indexType, autoDefineType);
    }

    public static boolean addColumn(final Credentials credentials, final String collection, final String columnName, final ColumnType columnType, final IndexType indexType, final AutoDefineType autoDefineType){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()){
            throw new InternalAdapterException("collection name must be specified");
        }

        if(columnName == null || columnName.isEmpty()) {
            throw new InternalAdapterException("column name must be specified");
        }

        if(columnType == null ){
            throw new InternalAdapterException("ColumnType must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", columnName);
        JsonObject typeJson = new JsonObject();
        typeJson.addProperty("type", columnType.getType());
        payloadJson.add("type", typeJson);

        if( autoDefineType != null )
            payloadJson.addProperty("auto-define", autoDefineType.getType());
        if( indexType != null )
            payloadJson.addProperty("index", indexType.getType());

        DbQueryResponse response = postStaticRequest(credentials, QueryType.ADD_COLUMN, collection, payloadJson);

        return response != null;
    }
    
    public static boolean dropColumn(final String collection, final String columnName){
        return dropColumn(Credentials.getInstance(), collection, columnName);
    }

    public static boolean dropColumn(final Credentials credentials, final String collection, final String columnName){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()){
            throw new InternalAdapterException("collection name must be specified");
        }

        if(columnName == null || columnName.isEmpty()) {
            throw new InternalAdapterException("column name must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", columnName);

        DbQueryResponse response = postStaticRequest(credentials, QueryType.DROP_COLUMN, collection, payloadJson);

        return response != null;
    }
    
    public static boolean createIndex(final String collection, final String columnName, final IndexType indexType){
        return createIndex(Credentials.getInstance(), collection, columnName, indexType);
    }

    public static boolean createIndex(final Credentials credentials, final String collection, final String columnName, final IndexType indexType){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()){
            throw new InternalAdapterException("collection name must be specified");
        }

        if(columnName == null || columnName.isEmpty()) {
            throw new InternalAdapterException("column name must be specified");
        }

        if(indexType == null){
            throw new InternalAdapterException("IndexType must be specified");
        }

        if(indexType == IndexType.NONE) {
            throw new InternalAdapterException("Cannot change IndexType to NONE. Use drop-index operation instead");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", columnName);
        payloadJson.addProperty("index", indexType.getType());

        DbQueryResponse response = postStaticRequest(credentials, QueryType.INDEX, collection, payloadJson);

        return response != null;
    }
    
    public static boolean dropIndex(final String collection, final String columnName){
        return dropIndex(Credentials.getInstance(), collection, columnName);
    }

    public static boolean dropIndex(final Credentials credentials, final String collection, final String columnName){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()){
            throw new InternalAdapterException("collection name must be specified");
        }

        if(columnName == null || columnName.isEmpty()){
            throw new InternalAdapterException("column name must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", columnName);

        DbQueryResponse response = postStaticRequest(credentials, QueryType.DROP_INDEX, collection, payloadJson);

        return response != null;
    }
    
    public static Iterator<Object> searchFiltered(final String collection, final String filterName, Object... params){
        return searchFiltered(Credentials.getInstance(), collection, filterName, params);
    }

    public static <T extends Db> Iterator<Object> searchFiltered(final Class<T> collectionClass, final String filterName, Object... params){
        if(collectionClass.getAnnotation(Entity.class) == null) {
            throw new InternalAdapterException("@Entity annotation must be specified on the collection class");
        }
        final Entity entity = (Entity) collectionClass.getAnnotation(Entity.class);

        final String collection = entity != null && entity.collection() != null && !"".equals(entity.collection()) ? entity.collection() : collectionClass.getSimpleName();
        if(collection == null || collection.isEmpty()) {
            throw new InternalAdapterException("could not identify a valid collection name from @Entity annotation");
        }

        return searchFiltered(Credentials.getInstance(), collection, filterName, params);
    }

    public static Iterator<Object> searchFiltered(final Credentials credentials, final String collection, final String filterName, final Object... params){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()){
            throw new InternalAdapterException("collection name must be specified");
        }

        if(filterName == null || filterName.isEmpty()) {
            throw new InternalAdapterException("filter name must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        Gson gson = new Gson();
        payloadJson.addProperty("name", filterName);
        payloadJson.addProperty("params", gson.toJson(params));

        final DbQueryResponse response = postStaticRequest(credentials, QueryType.SEARCH_FILTERED, collection, payloadJson);

        reportIfError(response);

        final JsonArray keysArray = response.getPayload().getAsJsonArray();
        List<Object> keys = new ArrayList<Object>();
        for(JsonElement key: keysArray){
            keys.add(key.getAsString());
        }
        return keys.iterator();
    }

    public static <T extends Db, U extends Object> U invokeProcedure(final String storedProcedureName, final Class<U> retClazz, final Object... params) {
        return invokeProcedure(Credentials.getInstance(), storedProcedureName, retClazz, params);
    }

    public static <T extends Db, U extends Object> U invokeProcedure(final Credentials credentials, final String storedProcedureName, final Class<U> retClazz, final Object... params) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(storedProcedureName == null || storedProcedureName.isEmpty()) {
            throw new InternalAdapterException("stored procedure name must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        Gson gson = new Gson();
        payloadJson.addProperty("name", storedProcedureName);
        payloadJson.addProperty("params", gson.toJson(params));

        // we dont need to pass any collection for this, we are passing a dummy collection
        // (bcoz I m too lazy to create a new function which is only called once)
        final DbQueryResponse  response = postStaticRequest(credentials, QueryType.STORED_PROC, "dummy", payloadJson);

        /* If ack:0 then check for error code and report accordingly */
        reportIfError(response);
        //todo proper handling here.
        // some things can return null also.
        U returnObj = gson.fromJson(response.getPayload().getAsString(), retClazz);
        return returnObj;
    }

    public static <T extends Db, U extends Object> U repopulateTable(final String collectionName, final Class<U> retClazz, final String... params) {
        return repopulateTable(Credentials.getInstance(), collectionName, retClazz, params);
    }

    public static <T extends Db, U extends Object> U repopulateTable(final Credentials credentials, final String collection, final Class<U> retClazz, final String... params) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()){
            throw new InternalAdapterException("collection name must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty(QueryConstants.TABLE, collection);
        payloadJson.addProperty("params", new Gson().toJson(params));

        final DbQueryResponse response = postStaticRequest(credentials, QueryType.REPOP_TABLE, collection, payloadJson);

        /* If ack:0 then check for error code and report accordingly */
        reportIfError(response);

        U returnObj = new Gson().fromJson(response.getPayload().getAsString(), retClazz);
        return returnObj;
    }
    
    public static void insert(final String collection, final String interpreterName, final String... data){
        insert(Credentials.getInstance(), collection, interpreterName, data);
    }
    
    public static void insert(final String collection, final String interpreterName, final List<String> data){
        if(data == null || data.isEmpty()) {
            throw new InternalAdapterException("cannot invoke an insert with interpreter on null or empty data");
        }

        insert(Credentials.getInstance(), collection, interpreterName, data.toArray(new String[data.size()]));
    }

    public static void insert(final Credentials credentials, final String collection, final String interpreterName, final List<String> data){
        if(data == null || data.isEmpty()) {
            throw new InternalAdapterException("cannot invoke an insert with interpreter on null or empty data");
        }

        insert(credentials, collection, interpreterName, data.toArray(new String[data.size()]));
    }

    public static void insert(final Credentials credentials, final String collection, final String interpreterName, final String... data){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(collection == null || collection.isEmpty()) {
            throw new InternalAdapterException("collection name must be specified");
        }

        if(interpreterName == null || interpreterName.isEmpty()) {
            throw new InternalAdapterException("interpreter name must be specified");
        }

        if(data == null) {
            throw new InternalAdapterException("cannot invoke an insert with interpreter on null data");
        }

        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("interpreter", interpreterName);
        JSONArray arr = new JSONArray(data);
        payloadJson.add("payload", new JsonParser().parse(arr.toString()).getAsJsonArray());
        final DbQueryResponse response = postStaticRequest(credentials, QueryType.INSERT_CUSTOM, collection, payloadJson);
        reportIfError(response);
    }

    public static <T extends Db> boolean contains(final Credentials credentials, final Class<T> clazz, final Object key) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        final DbQueryResponse response = postStaticRequest(credentials, clazz, QueryType.CONTAINS, key);

        if (response.isSuccessful()) {
            final JsonArray resultJsonArray = response.getPayload().getAsJsonArray();
            final int resultCount = resultJsonArray.size();
            final List<T> responseList = new ArrayList<T>();
            final String tableName = getCollection(clazz);
            final String dbName = getDs(clazz);
            CollectionStore.getInstance().registerClass(dbName, tableName, clazz);
            
            final Map<String, Field> structureMap = CollectionStore.getInstance().getStructure(dbName, tableName);
            for (int i = 0; i < resultCount; i++) {
                final T instance = Db.newInstance(clazz);
                final JsonObject instanceData = resultJsonArray.get(i).getAsJsonObject();
                final Set<Map.Entry<String, JsonElement>> entrySet = instanceData.entrySet();

                for (final Map.Entry<String, JsonElement> entry : entrySet) {
                    final String columnName = entry.getKey();

                    final Field field = structureMap.get(columnName);
                    if(field == null) {
                        continue;
                    }
                    // Field field = structureMap.get(columnName);
                    synchronized (field) {
                        final boolean oldAccessibilityValue = field.isAccessible();
                        field.setAccessible(true);

                        try {
                            field.set(instance, getCastedValue(field, instanceData.get(columnName), clazz));
                        } catch (IllegalArgumentException ex) {
                            throw new InternalAdapterException("Unable to set data into field \"" + clazz.getSimpleName() + "." + field.getName() + "\"", ex);
                        } catch (IllegalAccessException ex) {
                            throw new InternalAdapterException("Unable to set data into field \"" + clazz.getSimpleName() + "." + field.getName() + "\"", ex);
                        } finally {
                            field.setAccessible(oldAccessibilityValue);
                        }
                    }
                }

                responseList.add(instance);
            }
            return response.contains();
        }

        throw new DbOperationException(response.getErrorCode(), response.getErrorCause());
    }

    public static <T extends Db> void remove(final Credentials credentials, Class<T> clazz, final Object pk) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if(pk == null) {
            throw new InternalAdapterException("primary key must be specified");
        }

        final DbQueryResponse response = postStaticRequest(credentials, clazz, QueryType.REMOVE, pk);
        if (!response.isSuccessful()) {
            throw new DbOperationException(response.getErrorCode(), response.getErrorCause());
        }
    }

    public static <T extends Db> List<JSONObject> searchAsJson(final Credentials credentials, final Query<T> query) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if (query.getFromTables() == null && query.getFromTables().isEmpty()) {
            throw new InternalAdapterException("No collection (table) name set. Collection Table name is a mandatory field queries.");
        }

        final String queryStr = query.asSql(credentials.getDb());

        final DbQueryResponse response = QueryExecuter.executeSql(DbQueryRequest.create(credentials, queryStr));

        if (response.isSuccessful()) {
            final JsonArray resultJsonArray = response.getPayload().getAsJsonArray();
            final int resultCount = resultJsonArray.size();
            final List<JSONObject> responseList = new ArrayList<JSONObject>();

            for (int i = 0; i < resultCount; i++) {
                responseList.add(new JSONObject(resultJsonArray.get(i).getAsJsonObject().toString()));
            }
            return responseList;
        }

        throw new DbOperationException(response.getErrorCode(), response.getErrorCause());
    }
    
    /**
     * Allows search queries to be performed as defined by
     * @see <a href="http://docs.blobcity.com/display/DB/Operations+on+data#Operationsondata-SEARCH">
     *     http://docs.blobcity.com/display/DB/Operations+on+data#Operationsondata-SEARCH</a>.
     *
     * Note: This return type is prone to update when support for multiple collection queries (joins) is introduced.
     *
     * @param <T> Any class reference which extends {@link Db}
     * @param credentials Credentials to be used for communicating with the database
     * @param query {@link SearchParam}s which are to be used to search for data
     * @return {@link List} of {@code T} that matches {@code searchParams}
     */
    public static <T extends Db> List<T> search(final Credentials credentials, final Query<T> query) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        if (query.getFromTables() == null && query.getFromTables().isEmpty()) {
            throw new InternalAdapterException("No collection (table) name set. Collection Table name is a mandatory field queries.");
        }

        final String queryStr = query.asSql(credentials.getDb());
        
        final DbQueryResponse response = QueryExecuter.executeSql(DbQueryRequest.create(credentials, queryStr));
        
        final Class<T> clazz = query.getFromTables().get(0);

        if (response.isSuccessful()) {
            final JsonArray resultJsonArray = response.getPayload().getAsJsonArray();
            final int resultCount = resultJsonArray.size();
            final List<T> responseList = new ArrayList<T>();
            final String tableName = getCollection(clazz);
            final String dbName = getDs(clazz);
            CollectionStore.getInstance().registerClass(dbName, tableName, clazz);
            final Map<String, Field> structureMap = CollectionStore.getInstance().getStructure(dbName, tableName);

            for (int i = 0; i < resultCount; i++) {
                final T instance = Db.newInstance(clazz);
                final JsonObject instanceData = resultJsonArray.get(i).getAsJsonObject();
                final Set<Map.Entry<String, JsonElement>> entrySet = instanceData.entrySet();

                for (final Map.Entry<String, JsonElement> entry : entrySet) {
                    final String columnName = entry.getKey();
                    final Field field = structureMap.get(columnName);
                    if(field == null) {
                        continue;
                    }
                    synchronized (field) {
                        final boolean oldAccessibilityValue = field.isAccessible();
                        field.setAccessible(true);

                        try {
                            field.set(instance, getCastedValue(field, instanceData.get(columnName), clazz));
                        } catch (IllegalArgumentException ex) {
                            throw new InternalAdapterException("Unable to set data into field \"" + clazz.getSimpleName() + "." + field.getName() + "\"", ex);
                        } catch (IllegalAccessException ex) {
                            throw new InternalAdapterException("Unable to set data into field \"" + clazz.getSimpleName() + "." + field.getName() + "\"", ex);
                        } finally {
                            field.setAccessible(oldAccessibilityValue);
                        }
                    }
                }

                responseList.add(instance);
            }
            return responseList;
        }

        throw new DbOperationException(response.getErrorCode(), response.getErrorCause());
    }

    // we need some intelligent idea to send large amount of data over network. Until then, this is of no use to us.
    public static <T extends Db> Iterator<T> searchFiltered(final Credentials credentials, final Class<T> clazz, final String filter, final Object... params){
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        JsonObject payloadJson = new JsonObject();
        Gson gson = new Gson();
        payloadJson.addProperty("name", filter);
        payloadJson.addProperty("full-data", Boolean.TRUE);
        payloadJson.addProperty("params", gson.toJson(params));
        
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        final String tableName = getDs(clazz);
        final DbQueryResponse response = postStaticRequest(credentials, QueryType.SEARCH_FILTERED, tableName, payloadJson);
        
        reportIfError(response);
        // Query successfull, proceeding...
        final JsonArray resultJsonArray = response.getPayload().getAsJsonArray();
        final int resultCount = resultJsonArray.size();
        final List<T> responseList = new ArrayList<T>();
        
        final String dbName = getDs(clazz);
        CollectionStore.getInstance().registerClass(dbName, tableName, clazz);
        final Map<String, Field> structureMap = CollectionStore.getInstance().getStructure(dbName, tableName);

        for (int i = 0; i < resultCount; i++) {
            final T instance = Db.newInstance(clazz);
            final JsonObject instanceData = resultJsonArray.get(i).getAsJsonObject();
            final Set<Map.Entry<String, JsonElement>> entrySet = instanceData.entrySet();

            for (final Map.Entry<String, JsonElement> entry : entrySet) {
                final String columnName = entry.getKey();
                final Field field = structureMap.get(columnName);
                if(field == null) {
                    continue;
                }
                synchronized (field) {
                    final boolean oldAccessibilityValue = field.isAccessible();
                    field.setAccessible(true);

                    try {
                        field.set(instance, getCastedValue(field, instanceData.get(columnName), clazz));
                    } catch (IllegalArgumentException ex) {
                        throw new InternalAdapterException("Unable to set data into field \"" + clazz.getSimpleName() + "." + field.getName() + "\"", ex);
                    } catch (IllegalAccessException ex) {
                        throw new InternalAdapterException("Unable to set data into field \"" + clazz.getSimpleName() + "." + field.getName() + "\"", ex);
                    } finally {
                        field.setAccessible(oldAccessibilityValue);
                    }
                }
            }
            responseList.add(instance);
        }
        return responseList.iterator();
    }

    //private post request methods
    private DbQueryResponse postRequest(final Credentials credentials, QueryType queryType) {
        if(credentials == null) {
            throw new InternalAdapterException("connection credentials must be specified");
        }

        try {
            final JsonObject queryJson = new JsonObject();
            queryJson.addProperty(QueryConstants.TABLE, collection);
            queryJson.addProperty(QueryConstants.QUERY, queryType.getQueryCode());

            final Credentials dbSpecificCredentials = ds != null ? Credentials.create(credentials, null, null, null, ds) : credentials;
            queryJson.addProperty(QueryConstants.DB, dbSpecificCredentials.getDb());

            switch (queryType) {
                case LOAD:
                case REMOVE:
                    queryJson.addProperty(QueryConstants.PRIMARY_KEY, get_id());
                    break;
                case INSERT:
                case SAVE:
                    JsonObject payloadJson = new JsonObject();
                    payloadJson.addProperty(QueryConstants.TYPE,"json");
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(toJson());
                    payloadJson.add(QueryConstants.DATA, jsonArray);
                    queryJson.add(QueryConstants.PAYLOAD, payloadJson);
                    break;
                default:
                    throw new InternalDbException("Attempting to executed unknown or unidentifed query");
            }

            final DbQueryResponse response = QueryExecuter.executeBql(DbQueryRequest.create(dbSpecificCredentials, queryJson.toString()));
            return response;
        } catch (IllegalArgumentException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        } catch (IllegalAccessException ex) {
            throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
        }
    }

    /** Used to post requests that are not related to collection or data operations. The credentials of the user are not required
     * to have a datastore level authorisation to execute this, but the connecting user must be authorized to carry out
     * operations that can created and delete data stores, as well as perform other system level operations such as
     * adding and deleting nodes from a cluster.
     *
     * @param credentials the credentials containing only username and password
     * @param queryType the {@link QueryType} of the query
     * @param payloadJson information specific to the corresponding query type
     * @return the received response, post execution of the request
     */
    private static DbQueryResponse postStaticRequest(final Credentials credentials, final QueryType queryType, final JsonObject payloadJson) {
        JsonObject queryJson = new JsonObject();
        queryJson.addProperty(QueryConstants.QUERY, queryType.getQueryCode());
        queryJson.addProperty(QueryConstants.USER, credentials.getUsername());
        queryJson.addProperty(QueryConstants.PASS, credentials.getPassword());
        if(credentials.getDb() != null && !credentials.getDb().isEmpty()) {
            queryJson.addProperty(QueryConstants.DB, credentials.getDb());
        }
        queryJson.add(QueryConstants.PAYLOAD, payloadJson);

        final DbQueryResponse response = QueryExecuter.executeBql(DbQueryRequest.create(credentials, queryJson.toString()));
        return response;
    }
    
    private static  DbQueryResponse postStaticRequest(final Credentials credentials, final QueryType queryType, final String table, final JsonObject payloadJson){
        JsonObject queryJson = new JsonObject();
        queryJson.addProperty(QueryConstants.DB, credentials.getDb());
        queryJson.addProperty(QueryConstants.TABLE, table);
        queryJson.addProperty(QueryConstants.QUERY, queryType.getQueryCode());
        queryJson.addProperty(QueryConstants.USER, credentials.getUsername());
        queryJson.addProperty(QueryConstants.PASS, credentials.getPassword());
        queryJson.add(QueryConstants.PAYLOAD, payloadJson);
        
        final DbQueryResponse response = QueryExecuter.executeBql(DbQueryRequest.create(credentials, queryJson.toString()));
        return response;
    }
    
    private static <T extends Db> DbQueryResponse postStaticRequest(final Credentials credentials, final Class<T> clazz, final QueryType queryType) {
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);

        final String tableName = entity != null && entity.collection() != null && !"".equals(entity.collection()) ? entity.collection() : clazz.getSimpleName();

        final boolean entityContainsDbName = entity != null && entity.ds() != null && !"".equals(entity.ds());
        final String db = entityContainsDbName ? entity.ds() : credentials.getDb(); // No NPEs here because entityContainsDbName handles that
        final Credentials dbSpecificCredentials = entityContainsDbName ? Credentials.create(credentials, null, null, null, db) : credentials;

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(QueryConstants.DB, db);
        jsonObject.addProperty(QueryConstants.TABLE, tableName);
        jsonObject.addProperty(QueryConstants.QUERY, queryType.getQueryCode());
        final String queryStr = jsonObject.toString();

        final DbQueryResponse response = QueryExecuter.executeBql(DbQueryRequest.create(dbSpecificCredentials, queryStr));
        return response;
    }
    
    private static <T extends Db> DbQueryResponse postStaticRequest(final Credentials credentials, final Class<T> clazz, final QueryType queryType, final Object pk) {
        final JsonObject queryJson = new JsonObject();
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);

        final String tableName = entity != null && entity.collection() != null && !"".equals(entity.collection()) ? entity.collection() : clazz.getSimpleName();
        final boolean entityContainsDbName = entity != null && entity.ds() != null && !"".equals(entity.ds());
        final String db = entityContainsDbName ? entity.ds() : credentials.getDb(); // No NPEs here because entityContainsDbName handles that
        final Credentials dbSpecificCredentials = entityContainsDbName ? Credentials.create(credentials, null, null, null, db) : credentials;

        queryJson.addProperty(QueryConstants.DB, db);
        queryJson.addProperty(QueryConstants.TABLE, tableName);
        queryJson.addProperty(QueryConstants.QUERY, queryType.getQueryCode());
        queryJson.addProperty(QueryConstants.PRIMARY_KEY, pk.toString());

        final DbQueryResponse response = QueryExecuter.executeBql(DbQueryRequest.create(dbSpecificCredentials, queryJson.toString()));
        return response;
    }
    

   
    // Private instance methods
    private static void reportIfError(final DbQueryResponse response) {
        if (!response.isSuccessful()) {
            throw response.createException();
        }
    }

    public boolean load(final Credentials credentials) {
        final DbQueryResponse response = postRequest(credentials, QueryType.LOAD);

        /* If ack:0 then check for error code and report accordingly */
        if (!response.isSuccessful()) {
            if ("DB200".equals(response.getErrorCode())) {
                return false;
            }

            throw response.createException();
        }

        fromJson(response.getPayload().getAsJsonObject());
        return true;
    }

    public void save(final Credentials credentials) {
        final DbQueryResponse responseJson = postRequest(credentials, QueryType.SAVE);
        reportIfError(responseJson);
    }

    public boolean insert(final Credentials credentials) {
        final DbQueryResponse response = postRequest(credentials, QueryType.INSERT);
        if (response.isSuccessful()) {
//            final JsonElement payloadJson = response.getPayload();
//            fromJson(payloadJson.getAsJsonObject());
            return true;
        }

        // If you're here, query has failed
        if ("DB201".equals(response.getErrorCode())) { // Data already exists, don't throw an exception!
            return false;
        }

        // All is lost, lets get some popcorn and enjoy the destruction of society
        throw response.createException();
    }

    public void remove(final Credentials credentials) {
        final DbQueryResponse response = postRequest(credentials, QueryType.REMOVE);

        /* If ack:0 then check for error code and report accordingly */
        if (!response.isSuccessful() && !"DB200".equals(response.getErrorCode())) {
            reportIfError(response);
        }
    }

    /**
     * Runs an SQL query on the database and gives back the response received as a raw JSONObject
     * @param sql the sql query to run
     * @return an instance of {@link JSONObject}
     */
    public JSONObject runSql(final String sql) {
        return runSql(Credentials.getInstance(), sql);
    }

    /**
     * Runs an SQL query with custom credentials on the database and gives back the response received as a raw JSONObject
     * @param credentials the sepcific connection credentials to use
     * @param sql the sql query to run
     * @return an instance of {@link JSONObject}
     */
    public JSONObject runSql(final Credentials credentials, final String sql) {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    /**
     * Instantiates current object with data from the provided {@link JsonObject}.
     *
     * Every column mentioned in the {@link Db} instance (as maintained by {@link CollectionStore}) will be loaded
     * with data. If any of these column name IDs do not exist in the provided {@link JsonObject}, an
     * {@link InternalDbException} will be thrown. If there are any issues whilst reflecting the data into the instance,
     * an {@link InternalAdapterException} will be thrown.
     *
     * If any data already exists the calling object in any field mapped as a column, the data will be overwritten and
     * lost.
     *
     * @param jsonData input {@link JsonObject} from which the data for the current instance are to be loaded.
     */
    private void fromJson(final JsonObject jsonData) {
        final Map<String, Field> structureMap = CollectionStore.getInstance().getStructure(ds, collection);

        for (final String columnName : structureMap.keySet()) {
            final Field field = structureMap.get(columnName);
            if(field == null) {
                continue;
            }
            try {
                if (jsonData.has(columnName)) {
                    setFieldValue(field, jsonData.get(columnName));
                }
            } catch (IllegalArgumentException ex) {
                throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
            } catch (IllegalAccessException ex) {
                throw new InternalAdapterException("An error has occurred in the adapter. Check stack trace for more details.", ex);
            }
        }
    }

    /**
     * Gets a JSON representation of the object. The column names are same as those loaded in {@link CollectionStore}
     *
     * @return {@link JsonObject} representing the entity class in its current state
     * @throws IllegalArgumentException if the specified object is not an instance of the class or interface declaring
     * the underlying field (or a subclass or implementor thereof).
     * @throws IllegalAccessException if this {@code Field} object is enforcing Java language access control and the
     * underlying field is inaccessible.
     */
    private JsonObject toJson() throws IllegalArgumentException, IllegalAccessException {
        final Map<String, Field> structureMap = CollectionStore.getInstance().getStructure(ds, collection);
        final JsonObject dataJson = new JsonObject();

        for (String columnName : structureMap.keySet()) {
            final Field field = structureMap.get(columnName);
            if(field == null) {
                continue;
            }
            synchronized (field) {
                final boolean accessible = field.isAccessible();

                field.setAccessible(true);

                try {
                    if (field.getType() == java.util.Date.class) {
                        dataJson.addProperty(columnName, field.get(this) != null ? ((java.util.Date) field.get(this)).getTime() : null);
                        continue;
                    } else if (field.getType() == java.sql.Date.class) {
                        dataJson.addProperty(columnName, field.get(this) != null ? ((java.sql.Date) field.get(this)).getTime() : null);
                        continue;
                    } else if (field.getType() == List.class
                            || field.getType() == ArrayList.class
                            || field.getType() == LinkedList.class
                            || field.getType() == Set.class
                            || field.getType() == HashSet.class
                            || field.getType() == SortedSet.class
                            || field.getType() == TreeSet.class
                            || field.getType().isArray()) {
                        dataJson.add(columnName, field.get(this) != null ? new Gson().toJsonTree(field.get(this)) : null);
                        continue;
                    }

                    dataJson.addProperty(columnName, field.get(this) != null ? field.get(this).toString() : null);
                } catch (IllegalAccessException iae) {
                    throw iae;
                } finally {
                    field.setAccessible(accessible);
                }
            }
        }

        return dataJson;
    }

    private Object getPrimaryKeyValue() throws IllegalArgumentException, IllegalAccessException {
        Map<String, Field> structureMap = CollectionStore.getInstance().getStructure(ds, collection);

//        for (String columnName : structureMap.keySet()) {
//            Field field = structureMap.get(columnName);
//            if(field == null) {
//                continue;
//            }
//            if (field.getAnnotation(Primary.class) != null) {
//                final boolean accessible = field.isAccessible();
//
//                field.setAccessible(true);
//                try {
//                    final Object value = field.get(this);
//                    return value;
//                } catch (IllegalAccessException iae) {
//                    throw iae;
//                } finally {
//                    field.setAccessible(accessible);
//                }
//            }
//        }

        return null;
    }

    /**
     * Sets field level values by ensuring appropriate conversion between the input type (JSON) and Java's inherent data
     * types.
     *
     * @see #getCastedValue(java.lang.reflect.Field, java.lang.Object)
     * @param field field in current {@link Object} that needs to be updated
     * @param value value to be set for the field
     * @throws IllegalAccessException if the underlying field being changed is final
     */
    private void setFieldValue(final Field field, final JsonElement value) throws IllegalAccessException {
        synchronized (field) {
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

    /**
     * Transforms data type of a column dynamically leveraging Java Type Erasure. Currently supports all types that can
     * be used as primary keys in tables.
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
     * Provides a standard service to cast input types from JSON's format
     * ({@link Integer}, {@link String}, {@link JsonArray} etc.) to Java's internal data types.
     *
     * @param field field in current {@link Object} that needs to be updated
     * @param value value to be set for the field
     * @param parentClazz {@link Class} value of the parent object for which the casted field is being requested. This
     * field is only required for proper error logging in case of exceptions
     * @return appropriately casted value
     */
    private static Object getCastedValue(final Field field, final JsonElement value, final Class<?> parentClazz) {
        final Class<?> type = field.getType();

        if (type == String.class) { // Pre-exit most common use cases
            return value.isJsonNull() ? null : value.getAsString();
        }

        if (type.isEnum()) {
            return "".equals(value.getAsString()) ? null : Enum.valueOf((Class<? extends Enum>) type, value.getAsString());
        }

        if (type == Character.TYPE || type == Character.class) {
            return value.getAsCharacter();
        }

        if (type == List.class
                || type == ArrayList.class
                || type == LinkedList.class
                || type == Set.class
                || type == HashSet.class
                || type == SortedSet.class
                || type == TreeSet.class
                || type.isArray()) { // doesn't always return inside this block, BEWARE!
            if (value.isJsonArray()) {
                final JsonArray arr = value.getAsJsonArray();
                final int length = arr.size();
                final List<Object> list = new ArrayList(length);

                for (int i = 0; i < length; i++) {
                    list.add(arr.get(i).getAsString());
                }
                return list;
            } else if ("".equals(value.getAsString()) || value.isJsonNull()) {
                return new ArrayList();
            }

            Logger.getLogger(Db.class.getName()).log(Level.WARNING, "Class of type \"{0}\" has field with name \"{1}\" and data type \"{2}\" for value to be set was \"{3}\" has a type of {4}. This will probably cause an exception.", new Object[]{parentClazz, field.getName(), type, value, value.getClass()});
        }

        /* Beyound this point empty string should return null as the below types cannot have empty string values */
        if ((value.getAsString().isEmpty() || value == null) && !field.getType().isPrimitive()) {
            return null;
        }

        if (type == Double.TYPE || type == Double.class) {
            if (value == null || value.getAsString().isEmpty()) {
                return 0.0;
            }
            return value.getAsDouble();
        }

        if (type == Float.TYPE || type == Float.class) {
            if (value == null || value.getAsString().isEmpty()) {
                return 0.0f;
            }
            return value.getAsFloat();
        }

        if (type == Boolean.TYPE || type == Boolean.class) {
            if (value == null || value.getAsString().isEmpty()) {
                return false;
            }
            return value.getAsBoolean();
        }

        if (type == BigDecimal.class) {
            if (value == null || value.getAsString().isEmpty()) {
                return BigDecimal.ZERO;
            }
            return value.getAsBigInteger();
        }

        if (type == java.util.Date.class) {
            if (value == null || value.getAsString().isEmpty()) {
                return null;
            }
            return new java.util.Date(value.getAsLong());
        }

        if (type == java.sql.Date.class) {
            if (value == null || value.getAsString().isEmpty()) {
                return null;
            }
            return new java.sql.Date(value.getAsLong());
        }

        if (type == Integer.TYPE || type == Integer.class) {
            if (value == null || value.getAsString().isEmpty()) {
                return 0;
            }
            return value.getAsInt();
        }

        if (type == Long.TYPE || type == Long.class) {
            if (value == null || value.getAsString().isEmpty()) {
                return 0;
            }
            return value.getAsLong();
        }

        // The if for List check does not always return a value. Be sure before putting any code below here
        // If weird types are left, lets get their String versions..
        return value.getAsString();
    } 
    
}

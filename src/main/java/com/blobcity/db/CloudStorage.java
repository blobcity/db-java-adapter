/**
 * Copyright 2011 - 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.enums.TableType;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the connection and query execution framework for performing operations on the BlobCity data
 * store. This class must be extended by any Model that represents a BlobCity Entity.
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
    private static final Boolean bStoredProcOrTrigger=false;

    public CloudStorage() {
        for (Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation instanceof Entity) {
                final Entity blobCityEntity = (Entity) annotation;
                table = blobCityEntity.table();
                // if no db is present in the entity
                if (StringUtil.isEmpty(blobCityEntity.db())) {
                    db = Credentials.getInstance().getDb();
                }
                else{
                    db = blobCityEntity.db();
                    String dbName = Credentials.getInstance().getDb();
                    if(dbName.equals("dummy")) {
                        Credentials.getInstance().setDb(db);
                    }
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
        if( db==null || db.isEmpty() ){
            throw new InternalAdapterException("No Database information found. Did u make a call to Credentials.init() ");
        }
        
        TableStore.getInstance().registerClass(db, table, this.getClass());
    }

    /**
     * Statically provides the db name for any instance/child of {@link CloudStorage} that is internally used by the
     * adapter for querying. Note, this method is used by the adapter internally for SQL queries and the logic here
     * should be kept in sync with the rest of the class to ensure db names are evaluated appropriately. This method can
     * be used for logging purposes where the db name for a class is required.
     *
     * @param <T> Any class reference which extends {@link CloudStorage}
     * @param clazz class reference who's db name is required
     * @return Name of the DB
     */
    public static <T extends CloudStorage> String getDbName(final Class<T> clazz) {
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        return entity != null && !StringUtil.isEmpty(entity.db()) ? entity.db() : Credentials.getInstance().getDb();
    }
    
    /**
     * Statically provides the table name for any instance/child of {@link CloudStorage} that is internally used by the
     * adapter for querying. Note, this method is not used by the adapter internally but the logic here, should be kept
     * in sync with the rest of the class to ensure table names are evaluated appropriately. This method can be used for
     * logging purposes where the table name for a class is required.
     *
     * @param <T> Any class reference which extends {@link CloudStorage}
     * @param clazz class reference who's table name is required
     * @return Name of the table
     */
    public static <T extends CloudStorage> String getTableName(final Class<T> clazz) {
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        return entity != null && !StringUtil.isEmpty(entity.table()) ? entity.table() : clazz.getSimpleName();
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
        final Field primaryKeyField = TableStore.getInstance().getPkField(db, table);
        if (primaryKeyField == null) {
            throw new InternalAdapterException("Missing mandatory @Primary annotation for entity " + table + " [" + this.getClass().getName() + "]");
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
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
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
    
    /**
     * Allows quick search queries on a single column. This method internally uses {@link #search(com.blobcity.db.search.Query)
     * }
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

    public static <T extends CloudStorage> List<Object> selectAll(Class<T> clazz) {
        return selectAll(clazz, Object.class);
    }

    public static <T extends CloudStorage, K extends Object> List<K> selectAll(final Class<T> clazz, final Class<K> returnTypeClazz) {
        final DbQueryResponse response = postStaticRequest(Credentials.getInstance(), clazz, QueryType.SELECT_ALL);

        if (response.isSuccessful()) {
            return response.getKeys();
        }

        throw new DbOperationException(response.getErrorCode(), response.getErrorCause());
    }

    public static <T extends CloudStorage> boolean contains(final Class<T> clazz, final Object key) {
        return contains(Credentials.getInstance(), clazz, key);
    }

    public static <T extends CloudStorage> void remove(Class<T> clazz, final Object pk) {
        remove(Credentials.getInstance(), clazz, pk);
    }

    /**
     * Allows search queries to be performed as defined by
     * {@link http://docs.blobcity.com/display/DB/Operations+on+data#Operationsondata-SEARCH}. This method internally
     * calls {@link #search(com.blobcity.db.config.Credentials, com.blobcity.db.search.Query)} with default credentials
     * from {@link Credentials#getInstance()}
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

        try {

            Class<?> cls = Class.forName( "com.blobcity.db.bquery.SQLExecutorBean" );

            if(!bStoredProcOrTrigger) {
                //bStoredProcOrTrigger = true;
                Credentials cr = Credentials.getInstanceNullOrNotNull();
                if(cr == null) {
                       cr = Credentials.getInstanceForStoredProc();
//                       System.out.println(" dbname0 = " +cr.getDb());
                       return search(cr, query);
                }
                else {
//                    System.out.println(" dbname1 = " +cr.getDb());
//                    System.out.println(" dbname2 = " + query.getDbName(query.getFromTables().get(0)));
                    if(cr.getDb().equals(query.getDbName(query.getFromTables().get(0)))) {
                        return search(cr, query);
                    }
                    else {
                        cr = Credentials.getInstanceForStoredProc();
//                        System.out.println(" dbname3 = " +cr.getDb());
                        return search(cr, query);
                    }
                }
                //return search(cr, query);
            }
            else {
                return search(Credentials.getInstance(), query);
            }
        }   catch (ClassNotFoundException ex) {
//            System.out.println("Could not find com.blobcity.db.query.SQLExecutorBean");
//            Logger.getLogger(CloudStorage.class.getName()).log(Level.WARNING, null, ex);
        }
        return search(Credentials.getInstance(), query);
    }

    public static DbQueryResponse execute(final String sql) {
        return execute(Credentials.getInstance(), sql);
    }
    
    public static <T extends CloudStorage> Object execute(final Query<T> query) {
        return execute(Credentials.getInstance(), query);
    }

    public static void insertJsonData(final String table, final JsonObject insertJson){
        insertJsonData(Credentials.getInstance(), table, insertJson);
    }
    
    public static boolean createTable(final String table){
        if( table == null || table.isEmpty() ){
            throw new InternalAdapterException("Table can't be empty");
        }
        return createTable(Credentials.getInstance(), table);
    }
    
    public static boolean createTable(final String table, final JsonObject jsonSchema){
        if(table == null || table.isEmpty() ){
            throw new InternalAdapterException("Table can't be empty");
        }
        if(jsonSchema == null ){
            throw new InternalAdapterException("Schema can't be empty or null");
        }
        return createTable(Credentials.getInstance(), table, jsonSchema);
    }
    
    public static boolean createTable(final String table, final TableType tableType, final ReplicationType replicationType, final Integer replicationFactor, final boolean flexibleSchema){
        if(table == null || table.isEmpty() ){
            throw new InternalAdapterException("Table can't be empty");
        }
        if( tableType == null || replicationType == null){
            throw new InternalAdapterException("TableType and ReplicationType can't be empty");
        }
        return createTable(Credentials.getInstance(), table, tableType, replicationType, replicationFactor, flexibleSchema);
    }
    
    public static boolean dropTable(final String table){
        if(table == null || table.isEmpty() ){
            throw new InternalAdapterException("Table can't be empty");
        }
        return dropTable(Credentials.getInstance(), table);
    }
    
    public static boolean truncateTable(final String table){
        if(table == null || table.isEmpty() ){
            throw new InternalAdapterException("Table can't be empty");
        }
        return truncateTable(Credentials.getInstance(), table);
    }
    
    public static boolean addColumn(final String table, final String columnName, final ColumnType columnType, final IndexType indexType, final AutoDefineType autoDefineType){
        if(table == null || table.isEmpty() || columnName == null || columnName.isEmpty() ){
            throw new InternalAdapterException("Table and columnName can't be empty");
        }
        if(columnType == null ){
            throw new InternalAdapterException("Column Type can't be null");
        }
        
        return addColumn(Credentials.getInstance(), table, columnName, columnType, indexType, autoDefineType);
    }
    
    public static boolean dropColumn(final String table, final String columnName){
        if(table == null || table.isEmpty() || columnName == null || columnName.isEmpty() ){
            throw new InternalAdapterException("Table and columnName can't be empty");
        }
        
        return dropColumn(Credentials.getInstance(), table, columnName);
    }
    
    public static boolean createIndex(final String table, final String columnName, final IndexType indexType){
        if(table == null || table.isEmpty() || columnName == null || columnName.isEmpty() ){
            throw new InternalAdapterException("Table and columnName can't be empty");
        }
        if(indexType == null  || indexType == IndexType.NONE){
            throw new InternalAdapterException("Column Type can't be null or none. Use Drop-index to remove indexing");
        }
        
        return createIndex(Credentials.getInstance(), table, columnName, indexType);
    }
    
    public static boolean dropIndex(final String table, final String columnName){
        if(table == null || table.isEmpty() || columnName == null || columnName.isEmpty() ){
            throw new InternalAdapterException("Table and columnName can't be empty");
        }
        
        return dropIndex(Credentials.getInstance(), table, columnName);
    }
    
    public static Iterator<Object> searchFiltered(final String tableName, final String filterName, Object... params){
        return searchFiltered(Credentials.getInstance(), tableName, filterName, params);
    }
    
    public static <T extends CloudStorage> Iterator<Object> searchFiltered(final Class<T> tableClass, final String filterName, Object... params){
        final Entity entity = (Entity) tableClass.getAnnotation(Entity.class);
        final String tableName = entity != null && entity.table() != null && !"".equals(entity.table()) ? entity.table() : tableClass.getSimpleName();
        return searchFiltered(Credentials.getInstance(), tableName, filterName, params);
    }

    public static <T extends CloudStorage, U extends Object> U invokeProcedure(final String storedProcedureName, final Class<U> retClazz, final Object... params) {
        return invokeProcedure(Credentials.getInstance(), storedProcedureName, retClazz, params);
    }

    public static <T extends CloudStorage, U extends Object> U repopulateTable(final String tableName, final Class<U> retClazz, final String... params) {
        return repopulateTable(Credentials.getInstance(), tableName, retClazz, params);
    }
    
    
   
    // private static methods
    private static <T extends CloudStorage> boolean contains(final Credentials credentials, final Class<T> clazz, final Object key) {
        final DbQueryResponse response = postStaticRequest(credentials, clazz, QueryType.CONTAINS, key);

        if (response.isSuccessful()) {
            final JsonArray resultJsonArray = response.getPayload().getAsJsonArray();
            final int resultCount = resultJsonArray.size();
            final List<T> responseList = new ArrayList<T>();
            final String tableName = getTableName(clazz);
            final String dbName = getDbName(clazz);
            TableStore.getInstance().registerClass(dbName, tableName, clazz);
            
            final Map<String, Field> structureMap = TableStore.getInstance().getStructure(dbName, tableName);
            for (int i = 0; i < resultCount; i++) {
                final T instance = CloudStorage.newInstance(clazz);
                final JsonObject instanceData = resultJsonArray.get(i).getAsJsonObject();
                final Set<Map.Entry<String, JsonElement>> entrySet = instanceData.entrySet();

                for (final Map.Entry<String, JsonElement> entry : entrySet) {
                    final String columnName = entry.getKey();

                    final Field field = structureMap.get(columnName);
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
    
    private static DbQueryResponse execute(final Credentials credentials, final String sql) {
        return QueryExecuter.executeSql(DbQueryRequest.create(credentials, sql));
    }
    
    private static <T extends CloudStorage> Object execute(final Credentials credentials, final Query<T> query) {
        if (query.getFromTables() == null && query.getFromTables().isEmpty()) {
            throw new InternalAdapterException("No table name set. Table name is a mandatory field queries.");
        }

        final String queryStr = query.asSql();

        final DbQueryResponse response = QueryExecuter.executeSql(DbQueryRequest.create(credentials, queryStr));

        final Class<T> clazz = query.getFromTables().get(0);

        if (response.isSuccessful()) {

            //TODO: Throw away code
            if (response.getPayload() instanceof JsonArray) {
                final JsonArray resultJsonArray = response.getPayload().getAsJsonArray();
                final int resultCount = resultJsonArray.size();
                final List<T> responseList = new ArrayList<T>();
                final String tableName = getTableName(clazz);
                final String dbName = getDbName(clazz);
                TableStore.getInstance().registerClass(dbName, tableName, clazz);
                final Map<String, Field> structureMap = TableStore.getInstance().getStructure(dbName, tableName);

                for (int i = 0; i < resultCount; i++) {
                    final T instance = CloudStorage.newInstance(clazz);
                    final JsonObject instanceData = resultJsonArray.get(i).getAsJsonObject();
                    final Set<Map.Entry<String, JsonElement>> entrySet = instanceData.entrySet();

                    for (final Map.Entry<String, JsonElement> entry : entrySet) {
                        final String columnName = entry.getKey();

                        final Field field = structureMap.get(columnName);
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
    
    private static void insertJsonData(final Credentials credentials, final String table, final JsonObject insertJson){
        final DbQueryResponse response = postStaticRequest(credentials, QueryType.INSERT, table, insertJson);
        reportIfError(response);
    }
    
    private static <T extends CloudStorage> void remove(final Credentials credentials, Class<T> clazz, final Object pk) {
        final DbQueryResponse response = postStaticRequest(credentials, clazz, QueryType.REMOVE, pk);

        if (!response.isSuccessful()) {
            throw new DbOperationException(response.getErrorCode(), response.getErrorCause());
        }
    }
    
    /**
     * Allows search queries to be performed as defined by
     * {@link http://docs.blobcity.com/display/DB/Operations+on+data#Operationsondata-SEARCH}.
     *
     * Note: This return type is prone to update when support for multiple table queries (joins) is introduced.
     *
     * @param <T> Any class reference which extends {@link CloudStorage}
     * @param credentials Credentials to be used for communicating with the database
     * @param query {@link SearchParam}s which are to be used to search for data
     * @return {@link List} of {@code T} that matches {@code searchParams}
     */
    private static <T extends CloudStorage> List<T> search(final Credentials credentials, final Query<T> query) {
        if (query.getFromTables() == null && query.getFromTables().isEmpty()) {
            throw new InternalAdapterException("No table name set. Table name is a mandatory field queries.");
        }

        final String queryStr = query.asSql();
        
        final DbQueryResponse response = QueryExecuter.executeSql(DbQueryRequest.create(credentials, queryStr));
        
        final Class<T> clazz = query.getFromTables().get(0);

        if (response.isSuccessful()) {
            final JsonArray resultJsonArray = response.getPayload().getAsJsonArray();
            System.out.println(resultJsonArray);
            final int resultCount = resultJsonArray.size();
            final List<T> responseList = new ArrayList<T>();
            final String tableName = getTableName(clazz);
            final String dbName = getDbName(clazz);
            TableStore.getInstance().registerClass(dbName, tableName, clazz);
            final Map<String, Field> structureMap = TableStore.getInstance().getStructure(dbName, tableName);

            for (int i = 0; i < resultCount; i++) {
                final T instance = CloudStorage.newInstance(clazz);
                final JsonObject instanceData = resultJsonArray.get(i).getAsJsonObject();
                final Set<Map.Entry<String, JsonElement>> entrySet = instanceData.entrySet();

                for (final Map.Entry<String, JsonElement> entry : entrySet) {
                    final String columnName = entry.getKey();

                    final Field field = structureMap.get(columnName);
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
    
    private static boolean createTable(final Credentials credentials, final String table){
        DbQueryResponse response = postStaticRequest(credentials, QueryType.CREATE_TABLE, table, null);
        return response != null;
    } 
    
    private static boolean createTable(final Credentials credentials, final String table, final JsonObject jsonSchema){
        // this needs to be changed in future to be nested inside the payload json and not as payloadjson itself.
        JsonObject payloadJson = jsonSchema;
        DbQueryResponse response = postStaticRequest(credentials, QueryType.CREATE_TABLE, table, payloadJson);
        
        return response != null;
    } 
    
    private static boolean createTable(final Credentials credentials, final String table, final TableType tableType, final ReplicationType replicationType, final Integer replicationFactor, final boolean flexibleSchema){
        JsonObject payloadJson = new JsonObject();
        JsonObject metaJson = new JsonObject();
        metaJson.addProperty("replication-type", replicationType.getType());
        metaJson.addProperty("replication-factor", replicationFactor);
        metaJson.addProperty("table-type", tableType.getType());
        metaJson.addProperty("flexible-schema", flexibleSchema);
        payloadJson.add("meta", metaJson);
        DbQueryResponse response = postStaticRequest(credentials, QueryType.CREATE_TABLE, table, payloadJson);
        
        return response != null;
    }
    
    private static boolean dropTable(final Credentials credentials, final String table){
        DbQueryResponse response = postStaticRequest(credentials, QueryType.DROP_TABLE, table, null);
        return response != null;
    }
    
    private static boolean truncateTable(final Credentials credentials, final String table){
        DbQueryResponse response = postStaticRequest(credentials, QueryType.TRUNCATE_TABLE, table, null);
        return response != null;
    }
    
    private static boolean addColumn(final Credentials credentials, final String table, final String columnName, final ColumnType columnType, final IndexType indexType, final AutoDefineType autoDefineType){
        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", columnName);
        JsonObject typeJson = new JsonObject();
        typeJson.addProperty("type", columnType.getType());
        payloadJson.add("type", typeJson);
        
        if( autoDefineType != null )
            payloadJson.addProperty("auto-define", autoDefineType.getType());
        if( indexType != null )
            payloadJson.addProperty("index", indexType.getType());
        
        DbQueryResponse response = postStaticRequest(credentials, QueryType.ADD_COLUMN, table, payloadJson);
        
        return response != null;
    }
   
    private static boolean dropColumn(final Credentials credentials, final String table, final String columnName){
        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", columnName);
        
        DbQueryResponse response = postStaticRequest(credentials, QueryType.DROP_COLUMN, table, payloadJson);
        
        return response != null;
    }
    
    private static boolean createIndex(final Credentials credentials, final String table, final String columnName, final IndexType indexType){
        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", columnName);
        payloadJson.addProperty("index", indexType.getType());
        
        DbQueryResponse response = postStaticRequest(credentials, QueryType.INDEX, table, payloadJson);
        
        return response != null;
    }
    
    private static boolean dropIndex(final Credentials credentials, final String table, final String columnName){
        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty("name", columnName);
        
        DbQueryResponse response = postStaticRequest(credentials, QueryType.DROP_INDEX, table, payloadJson);
        
        return response != null;
    }
    
    private static <T extends CloudStorage, U extends Object> U invokeProcedure(final Credentials credentials, final String storedProcedureName, final Class<U> retClazz, final Object... params) {
        JsonObject payloadJson = new JsonObject();
        Gson gson = new Gson();
        payloadJson.addProperty("name", storedProcedureName);
        payloadJson.addProperty("params", gson.toJson(params));
        
        // we dont need to pass any table for this, we are passing a dummy table 
        // (bcoz I m too lazy to create a new function which is only called once)
        final DbQueryResponse  response = postStaticRequest(credentials, QueryType.STORED_PROC, "dummy", payloadJson);

        /* If ack:0 then check for error code and report accordingly */
        reportIfError(response);
        //todo proper handling here.
        // some things can return null also.
        U returnObj = gson.fromJson(response.getPayload().getAsString(), retClazz);
        return returnObj;
    }

    private static Iterator<Object> searchFiltered(final Credentials credentials, final String tableName, final String filterName, final Object... params){
        JsonObject payloadJson = new JsonObject();
        Gson gson = new Gson();
        payloadJson.addProperty("name", filterName);
        payloadJson.addProperty("params", gson.toJson(params));
        
        final DbQueryResponse response = postStaticRequest(credentials, QueryType.SEARCH_FILTERED, tableName, payloadJson);
        
        reportIfError(response);
        
        final JsonArray keysArray = response.getPayload().getAsJsonArray();
        List<Object> keys = new ArrayList<Object>();
        for(JsonElement key: keysArray){
            keys.add(key.getAsString());
        }
        return keys.iterator();
    }
    
    private static <T extends CloudStorage, U extends Object> U repopulateTable(final Credentials credentials, final String tableName, final Class<U> retClazz, final String... params) {
        JsonObject payloadJson = new JsonObject();
        payloadJson.addProperty(QueryConstants.TABLE, tableName);
        payloadJson.addProperty("params", new Gson().toJson(params));
        
        final DbQueryResponse response = postStaticRequest(credentials, QueryType.REPOP_TABLE, tableName, payloadJson);

        /* If ack:0 then check for error code and report accordingly */
        reportIfError(response);

        U returnObj = new Gson().fromJson(response.getPayload().getAsString(), retClazz);
        return returnObj;
    }
    
    // we need some intelligent idea to send large amount of data over network. Until then, this is of no use to us.
    private static <T extends CloudStorage> Iterator<T> searchFiltered(final Credentials credentials, final Class<T> clazz, final String filterName, final Object... params){
        JsonObject payloadJson = new JsonObject();
        Gson gson = new Gson();
        payloadJson.addProperty("name", filterName);
        payloadJson.addProperty("full-data", Boolean.TRUE);
        payloadJson.addProperty("params", gson.toJson(params));
        
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        final String tableName = getDbName(clazz);
        final DbQueryResponse response = postStaticRequest(credentials, QueryType.SEARCH_FILTERED, tableName, payloadJson);
        
        reportIfError(response);
        // Query successfull, proceeding...
        final JsonArray resultJsonArray = response.getPayload().getAsJsonArray();
        final int resultCount = resultJsonArray.size();
        final List<T> responseList = new ArrayList<T>();
        
        final String dbName = getDbName(clazz);
        TableStore.getInstance().registerClass(dbName, tableName, clazz);
        final Map<String, Field> structureMap = TableStore.getInstance().getStructure(dbName, tableName);

        for (int i = 0; i < resultCount; i++) {
            final T instance = CloudStorage.newInstance(clazz);
            final JsonObject instanceData = resultJsonArray.get(i).getAsJsonObject();
            final Set<Map.Entry<String, JsonElement>> entrySet = instanceData.entrySet();

            for (final Map.Entry<String, JsonElement> entry : entrySet) {
                final String columnName = entry.getKey();
                final Field field = structureMap.get(columnName);
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
        try {
            final JsonObject queryJson = new JsonObject();
            queryJson.addProperty(QueryConstants.TABLE, table);
            queryJson.addProperty(QueryConstants.QUERY, queryType.getQueryCode());

            final Credentials dbSpecificCredentials = db != null ? Credentials.create(credentials, null, null, null, db) : credentials;
            queryJson.addProperty(QueryConstants.DB, dbSpecificCredentials.getDb());

            switch (queryType) {
                case LOAD:
                case REMOVE:
                    queryJson.addProperty(QueryConstants.PRIMARY_KEY, getPrimaryKeyValue().toString());
                    break;
                case INSERT:
                case SAVE:
                    queryJson.add(QueryConstants.PAYLOAD, toJson());
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
    
    private static <T extends CloudStorage> DbQueryResponse postStaticRequest(final Credentials credentials, final Class<T> clazz, final QueryType queryType) {
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);

        final String tableName = entity != null && entity.table() != null && !"".equals(entity.table()) ? entity.table() : clazz.getSimpleName();

        final boolean entityContainsDbName = entity != null && entity.db() != null && !"".equals(entity.db());
        final String db = entityContainsDbName ? entity.db() : credentials.getDb(); // No NPEs here because entityContainsDbName handles that
        final Credentials dbSpecificCredentials = entityContainsDbName ? Credentials.create(credentials, null, null, null, db) : credentials;

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(QueryConstants.DB, db);
        jsonObject.addProperty(QueryConstants.TABLE, tableName);
        jsonObject.addProperty(QueryConstants.QUERY, queryType.getQueryCode());
        final String queryStr = jsonObject.toString();

        final DbQueryResponse response = QueryExecuter.executeBql(DbQueryRequest.create(dbSpecificCredentials, queryStr));
        return response;
    }
    
    private static <T extends CloudStorage> DbQueryResponse postStaticRequest(final Credentials credentials, final Class<T> clazz, final QueryType queryType, final Object pk) {
        final JsonObject queryJson = new JsonObject();
        final Entity entity = (Entity) clazz.getAnnotation(Entity.class);

        final String tableName = entity != null && entity.table() != null && !"".equals(entity.table()) ? entity.table() : clazz.getSimpleName();
        final boolean entityContainsDbName = entity != null && entity.db() != null && !"".equals(entity.db());
        final String db = entityContainsDbName ? entity.db() : credentials.getDb(); // No NPEs here because entityContainsDbName handles that
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

    private boolean load(final Credentials credentials) {
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

    private void save(final Credentials credentials) {
        final DbQueryResponse responseJson = postRequest(credentials, QueryType.SAVE);
        reportIfError(responseJson);
    }

    private boolean insert(final Credentials credentials) {
        final DbQueryResponse response = postRequest(credentials, QueryType.INSERT);
        if (response.isSuccessful()) {
            final JsonElement payloadJson = response.getPayload();
            fromJson(payloadJson.getAsJsonObject());
            return true;
        }

        // If you're here, query has failed
        if ("DB201".equals(response.getErrorCode())) { // Data already exists, don't throw an exception!
            return false;
        }

        // All is lost, lets get some popcorn and enjoy the destruction of society
        throw response.createException();
    }

    private void remove(final Credentials credentials) {
        final DbQueryResponse response = postRequest(credentials, QueryType.REMOVE);

        /* If ack:0 then check for error code and report accordingly */
        if (!response.isSuccessful() && !"DB200".equals(response.getErrorCode())) {
            reportIfError(response);
        }
    }
    
    /**
     * Instantiates current object with data from the provided {@link JsonObject}.
     *
     * Every column mentioned in the {@link CloudStorage} instance (as maintained by {@link TableStore}) will be loaded
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
        final Map<String, Field> structureMap = TableStore.getInstance().getStructure(db, table);

        for (final String columnName : structureMap.keySet()) {
            final Field field = structureMap.get(columnName);

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
     * Gets a JSON representation of the object. The column names are same as those loaded in {@link TableStore}
     *
     * @return {@link JsonObject} representing the entity class in its current state
     * @throws IllegalArgumentException if the specified object is not an instance of the class or interface declaring
     * the underlying field (or a subclass or implementor thereof).
     * @throws IllegalAccessException if this {@code Field} object is enforcing Java language access control and the
     * underlying field is inaccessible.
     */
    private JsonObject toJson() throws IllegalArgumentException, IllegalAccessException {
        final Map<String, Field> structureMap = TableStore.getInstance().getStructure(db, table);
        final JsonObject dataJson = new JsonObject();

        for (String columnName : structureMap.keySet()) {
            final Field field = structureMap.get(columnName);
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
        Map<String, Field> structureMap = TableStore.getInstance().getStructure(db, table);

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

            Logger.getLogger(CloudStorage.class.getName()).log(Level.WARNING, "Class of type \"{0}\" has field with name \"{1}\" and data type \"{2}\" for value to be set was \"{3}\" has a type of {4}. This will probably cause an exception.", new Object[]{parentClazz, field.getName(), type, value, value.getClass()});
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

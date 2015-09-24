/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.exceptions.InternalAdapterException;
import com.blobcity.db.annotations.Column;
import com.blobcity.db.annotations.Primary;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Caches table structures, so that annotations are not require to be processed on every operation.
 * 
 * New:
 * instead of using just table name, we are using tableName.RandomUUID as key to reduce chances of collision
 *
 * @author Sanket Sarang
 * @author Karun AB <karun.ab@blobcity.net>
 * @author Prikshit Kumar <prikshit.kumar@blobcity.com>
 */
class TableStore {
    // outer key is mapped on dbName.tableName instead of just tableName
    private final Map<String, Map<String, Field>> tableStructureMap;
    private final Map<String, Class<? extends CloudStorage>> tableClassMap;
    private final Map<String, Field> tablePrimaryMap;

    private TableStore() {
        this.tablePrimaryMap = new HashMap<String, Field>();
        this.tableClassMap = new HashMap<String, Class<? extends CloudStorage>>();
        this.tableStructureMap = new HashMap<String, Map<String, Field>>();
    }

    public static TableStore getInstance() {
        return TableStoreHolder.INSTANCE;
    }

    private static class TableStoreHolder {

        private static final TableStore INSTANCE = new TableStore();
    }

    public <T extends CloudStorage> void registerClass(String dbName, String tableName, Class<T> clazz) {
        String key = dbName + "." + tableName;
        tableClassMap.put(key, clazz);
    }
    
    /**
     * returns the structure of table given tableName and className
     * @param tableName
     * @param className
     * @return null if no such table is registered
     */
    public Map<String, Field> getStructure(final String dbName, final String tableName) {
        String key = dbName + "." + tableName;
        if(!tableStructureMap.containsKey(key)){
            loadTableStructure(key);
        }
        return tableStructureMap.get(key);
    }
    
    /**
     * 
     * @param tableName : name of table for which class belongs to
     * @param className : name of class of table
     * @return null if there is no table is registered
     *          primary field if table is registered
     */
    public Field getPkField(final String dbName, final String tableName) {
        String key = dbName + "." + tableName;
        if(!tableStructureMap.containsKey(key)){
            loadTableStructure(key);
        }
        return tablePrimaryMap.get(key);
    }

    /**
     * Loads the structure of a class into maps defined earlier
     * 
     * @param key: tableName.randomUUID
     */
    private void loadTableStructure(final String key) {
        final Field[] fields = tableClassMap.get(key).getDeclaredFields();
        final Map<String, Field> columnFieldMap = new HashMap<String, Field>();
        final Map<String, Field> allFieldMap = new HashMap<String, Field>();
        Field primaryKeyField = null;

        for (final Field field : fields) {
            String columnName = field.getName();
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            for (Annotation a : field.getAnnotations()) {
                if (a instanceof Column) {
                    final Column column = (Column) a;
                    if (column.name() != null && !"".equals(column.name())) {
                        columnName = column.name();
                    }

                    columnFieldMap.put(columnName, field);
                } else if (a instanceof Primary) {
                    if (primaryKeyField != null) {
                        throw new InternalAdapterException("Repetition of primary key annotation in table: " + key
                                + ". Repeat value found for fields " + primaryKeyField.getName() + " and " + field.getName()
                                + ". The @Primary annotation may be applied to only one field in an entity class");
                    }
                    primaryKeyField = field;
                }
            }

            allFieldMap.put(columnName, field);
        }

        tablePrimaryMap.put(key, primaryKeyField);
        tableStructureMap.put(key, columnFieldMap.isEmpty() ? allFieldMap : columnFieldMap);
    }

}

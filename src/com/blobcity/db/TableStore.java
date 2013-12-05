/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.Primary;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Caches table structures, so that annotations are not require to be processed on every operation.
 *
 * @author Sanket Sarang
 */
class TableStore {

    private Map<String, Map<String, Field>> tableStructureMap = new HashMap<String, Map<String, Field>>();
    private Map<String, Class> tableClassMap = new HashMap<String, Class>();
    private Map<String, Field> tablePrimaryMap = new HashMap<String, Field>();

    private TableStore() {
    }

    public static TableStore getInstance() {
        return TableStoreHolder.INSTANCE;
    }

    private static class TableStoreHolder {

        private static final TableStore INSTANCE = new TableStore();
    }

    public void registerClass(String name, Class clazz) {
        tableClassMap.put(name, clazz);
    }

    public Map<String, Field> getStructure(final String tableName) {
        if (!tableStructureMap.containsKey(tableName)) {
            loadTableStructure(tableName);
        }

        return tableStructureMap.get(tableName);
    }

    public Field getPkField(final String tableName) {
        if (!tableStructureMap.containsKey(tableName)) {
            loadTableStructure(tableName);
        }

        return tablePrimaryMap.get(tableName);
    }

    private void loadTableStructure(final String tableName) {
        if (!tableClassMap.containsKey(tableName)) {
            return;
        }

        Field[] fields = tableClassMap.get(tableName).getDeclaredFields();
        Map<String, Field> columnFieldMap = new HashMap<String, Field>();
        Field primaryKeyField = null;
        for (Field field : fields) {
            String columnName = field.getName();

            for (Annotation a : field.getAnnotations()) {
                if (a instanceof Column) {
                    Column column = (Column) a;
                    columnName = column.name();
                } else if (a instanceof Primary) {
                    if (primaryKeyField != null) {
                        throw new RuntimeException("Possible repetition of primary key annotation in table: " + tableName
                                + ". Reapeat value found for fields " + primaryKeyField.getName() + " and " + field.getName()
                                + ". The @Primary annotation may be applied to only one field in an entity class");
                    }
                    primaryKeyField = field;
                }
            }
            columnFieldMap.put(columnName, field);
        }

        tablePrimaryMap.put(tableName, primaryKeyField);
        tableStructureMap.put(tableName, columnFieldMap);
    }
    
//      private void loadTableStructure(final String tableName) {
//        if (!tableClassMap.containsKey(tableName)) {
//            return;
//        }
//        Field[] fields = tableClassMap.get(tableName).getDeclaredFields();
//        Map<String, Field> columnFieldMap = new HashMap<String, Field>();
//        Field primaryKeyField = null;
//        for (Field field : fields) {
//            boolean isColumn = false;
//            String columnName = null;
//            for (Annotation a : field.getAnnotations()) {
//                if (a instanceof Column) {
//                    Column column = (Column) a;
//                    columnName = column.name();
//                    isColumn = true;
//                } else if (a instanceof Primary && primaryKeyField == null) {
//                    primaryKeyField = field;
//                }
//            }
//            if (isColumn) {
//                columnFieldMap.put(columnName, field);
//            }
//        }
//        tablePrimaryMap.put(tableName, primaryKeyField);
//        tableStructureMap.put(tableName, columnFieldMap);
//    }
}
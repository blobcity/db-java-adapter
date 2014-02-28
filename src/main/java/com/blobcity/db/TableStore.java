/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.exceptions.InternalAdapterException;
import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.Primary;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Caches table structures, so that annotations are not require to be processed on every operation.
 *
 * @author Sanket Sarang
 * @author Karun AB <karun.ab@blobcity.net>
 */
class TableStore {

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

    public <T extends CloudStorage> void registerClass(String name, Class<T> clazz) {
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

        final Field[] fields = tableClassMap.get(tableName).getDeclaredFields();
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
                        throw new InternalAdapterException("Repetition of primary key annotation in table: " + tableName
                                + ". Repeat value found for fields " + primaryKeyField.getName() + " and " + field.getName()
                                + ". The @Primary annotation may be applied to only one field in an entity class");
                    }
                    primaryKeyField = field;
                }
            }

            allFieldMap.put(columnName, field);
        }

        tablePrimaryMap.put(tableName, primaryKeyField);
        tableStructureMap.put(tableName, columnFieldMap.isEmpty() ? allFieldMap : columnFieldMap);
    }
}

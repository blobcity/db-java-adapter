/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.annotations.Column;
import com.blobcity.db.annotations.Primary;
import com.blobcity.db.exceptions.InternalAdapterException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Caches collection structures, so that annotations are not require to be processed on every operation.
 * <p>
 * New:
 * instead of using just collection name, we are using tableName.RandomUUID as key to reduce chances of collision
 *
 * @author Sanket Sarang
 * @author Karun AB
 * @author Prikshit Kumar
 */
class CollectionStore {
  // outer key is mapped on dbName.tableName instead of just tableName
  private final Map<String, Map<String, Field>> tableStructureMap;
  private final Map<String, Class<? extends Db>> tableClassMap;
  private final Map<String, Field> tablePrimaryMap;

  private CollectionStore() {
    this.tablePrimaryMap = new HashMap<String, Field>();
    this.tableClassMap = new HashMap<String, Class<? extends Db>>();
    this.tableStructureMap = new HashMap<String, Map<String, Field>>();
  }

  public static CollectionStore getInstance() {
    return TableStoreHolder.INSTANCE;
  }

  public <T extends Db> void registerClass(String dbName, String tableName, Class<T> clazz) {
    String key = dbName + "." + tableName;
    tableClassMap.put(key, clazz);
  }

  /**
   * returns the structure of collection given tableName and className
   *
   * @param tableName
   * @param className
   * @return null if no such collection is registered
   */
  public Map<String, Field> getStructure(final String dbName, final String tableName) {
    String key = dbName + "." + tableName;
    if (!tableStructureMap.containsKey(key)) {
      loadTableStructure(key);
    }
    return tableStructureMap.get(key);
  }

  /**
   * @param tableName : name of collection for which class belongs to
   * @param className : name of class of collection
   * @return null if there is no collection is registered
   * primary field if collection is registered
   */
  public Field getPkField(final String dbName, final String tableName) {
    String key = dbName + "." + tableName;
    if (!tableStructureMap.containsKey(key)) {
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
            throw new InternalAdapterException("Repetition of primary key annotation in collection: " + key
              + ". Repeat value found for fields " + primaryKeyField.getName() + " and " + field.getName()
              + ". The @Primary annotation may be applied to only one field in an entity class");
          }
          primaryKeyField = field;
        }
      }

      allFieldMap.put(columnName, field);
    }

    if (primaryKeyField == null) {
      try {
        primaryKeyField = tableClassMap.get(key).getSuperclass().getDeclaredField("_id");
        if (columnFieldMap.isEmpty()) {
          allFieldMap.put("_id", primaryKeyField);
        } else {
          columnFieldMap.put("_id", primaryKeyField);
        }
      } catch (NoSuchFieldException e) {
        //do nothing
      }
    }

    tablePrimaryMap.put(key, primaryKeyField);
    tableStructureMap.put(key, columnFieldMap.isEmpty() ? allFieldMap : columnFieldMap);
  }

  private static class TableStoreHolder {

    private static final CollectionStore INSTANCE = new CollectionStore();
  }

}

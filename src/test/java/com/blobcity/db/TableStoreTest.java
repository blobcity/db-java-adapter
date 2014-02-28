/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */

package com.blobcity.db;

import com.blobcity.db.model.TestModelAllColumns;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.Test;
import com.blobcity.db.model.TestModelNoColumns;
import com.blobcity.db.model.TestModelSomeColumns;
import com.blobcity.db.model.TestModelWeirdColumns;
import com.blobcity.db.model.TestModelSomeWeirdColumns;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test class for {@link TableStore}
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
public class TableStoreTest {

    private TableStore tableStore;

    @Before
    public void beforeTest() {
        tableStore = TableStore.getInstance();
    }

    @After
    public void afterTest() {
        tableStore = null;
    }

    /**
     * Test of getStructure method, of class TableStore.
     */
    @Test
    public void testGetStructure() {
        final String methodName = "TableStore.getInstance(): ";
        System.out.println(methodName + "started");

        {
            final String tableName = TestModelNoColumns.TABLE_NAME_REFERENCE;
            tableStore.registerClass(tableName, TestModelNoColumns.class);
            final Map<String, Field> resultStructure = tableStore.getStructure(tableName);
            assertNotNull("Field \"key\" should be visible in table " + TestModelNoColumns.class, resultStructure.get("key"));
            assertNotNull("Field \"value\" should be visible in table " + TestModelNoColumns.class, resultStructure.get("value"));
            assertNotNull("Field \"crazy\" should be visible in table " + TestModelNoColumns.class, resultStructure.get("crazy"));
            assertNull("Field \"logger\" shouldn't have been visible in table " + TestModelNoColumns.class + " because it is transient.", resultStructure.get("logger"));
            assertNull("Field \"TABLE_NAME_REFERENCE\" shouldn't have been visible in table " + TestModelNoColumns.class + " because it is static.", resultStructure.get("TABLE_NAME_REFERENCE"));
        }
        {
            final String tableName = TestModelAllColumns.TABLE_NAME_REFERENCE;
            tableStore.registerClass(tableName, TestModelAllColumns.class);
            final Map<String, Field> resultStructure = tableStore.getStructure(tableName);
            assertNotNull("Field \"key\" should be visible in table " + TestModelAllColumns.class, resultStructure.get("key"));
            assertNotNull("Field \"value\" should be visible in table " + TestModelAllColumns.class, resultStructure.get("value"));
            assertNotNull("Field \"crazy\" should be visible in table " + TestModelAllColumns.class, resultStructure.get("crazy"));
            assertNull("Field \"logger\" shouldn't have been visible in table " + TestModelAllColumns.class + " because it is transient.", resultStructure.get("logger"));
            assertNull("Field \"TABLE_NAME_REFERENCE\" shouldn't have been visible in table " + TestModelAllColumns.class + " because it is static.", resultStructure.get("TABLE_NAME_REFERENCE"));
        }
        {
            final String tableName = TestModelWeirdColumns.TABLE_NAME_REFERENCE;
            tableStore.registerClass(tableName, TestModelWeirdColumns.class);
            final Map<String, Field> resultStructure = tableStore.getStructure(tableName);
            assertNotNull("Field \"key\" should be visible in table " + TestModelWeirdColumns.class, resultStructure.get("key"));
            assertNotNull("Field \"value\" should be visible in table " + TestModelWeirdColumns.class, resultStructure.get("value"));
            assertNotNull("Field \"crazy\" should be visible in table " + TestModelWeirdColumns.class, resultStructure.get("crazy"));
            assertNull("Field \"logger\" shouldn't have been visible in table " + TestModelWeirdColumns.class + " because it is transient.", resultStructure.get("logger"));
            assertNull("Field \"TABLE_NAME_REFERENCE\" shouldn't have been visible in table " + TestModelWeirdColumns.class + " because it is static.", resultStructure.get("TABLE_NAME_REFERENCE"));
        }
        {
            final String tableName = TestModelSomeColumns.TABLE_NAME_REFERENCE;
            tableStore.registerClass(tableName, TestModelSomeColumns.class);
            final Map<String, Field> resultStructure = tableStore.getStructure(tableName);
            assertNotNull("Field \"key\" should be visible in table " + TestModelSomeColumns.class, resultStructure.get("key"));
            assertNotNull("Field \"value\" should be visible in table " + TestModelSomeColumns.class, resultStructure.get("value"));
            assertNull("Field \"crazy\" shouldn't have been visible in table " + TestModelSomeColumns.class, resultStructure.get("crazy"));
            assertNull("Field \"logger\" shouldn't have been visible in table " + TestModelSomeColumns.class + " because it is transient.", resultStructure.get("logger"));
            assertNull("Field \"TABLE_NAME_REFERENCE\" shouldn't have been visible in table " + TestModelSomeColumns.class + " because it is static.", resultStructure.get("TABLE_NAME_REFERENCE"));
        }
        {
            final String tableName = TestModelSomeWeirdColumns.TABLE_NAME_REFERENCE;
            tableStore.registerClass(tableName, TestModelSomeWeirdColumns.class);
            final Map<String, Field> resultStructure = tableStore.getStructure(tableName);
            assertNull("Field \"key\" should be visible in table " + TestModelSomeWeirdColumns.class, resultStructure.get("key"));
            assertNotNull("Field \"value\" should be visible in table " + TestModelSomeWeirdColumns.class, resultStructure.get("value"));
            assertNull("Field \"crazy\" shouldn't have been visible in table " + TestModelSomeWeirdColumns.class, resultStructure.get("crazy"));
            assertNull("Field \"logger\" shouldn't have been visible in table " + TestModelSomeWeirdColumns.class + " because it is transient.", resultStructure.get("logger"));
            assertNull("Field \"TABLE_NAME_REFERENCE\" shouldn't have been visible in table " + TestModelSomeWeirdColumns.class + " because it is static.", resultStructure.get("TABLE_NAME_REFERENCE"));
        }

        System.out.println(methodName + "ended");
    }

    /**
     * Test of getPkField method, of class TableStore.
     */
    @Test
    public void testGetPkField() {
        final String methodName = "TableStore.getPkField(): ";
        System.out.println(methodName + "started");
        {
            final String tableName = TestModelSomeColumns.TABLE_NAME_REFERENCE;
            String expResult = "key";
            tableStore.registerClass(tableName, TestModelSomeColumns.class);
            final Field result = tableStore.getPkField(tableName);
            assertEquals(expResult, result.getName());
        }
        {
            final String tableName = TestModelWeirdColumns.TABLE_NAME_REFERENCE;
            tableStore.registerClass(tableName, TestModelWeirdColumns.class);
            final Field result = tableStore.getPkField(tableName);
            assertNull("Expected the column for " + TestModelWeirdColumns.class + " to be null but is instead " + (result != null ? result.getName() : "null") + ".", result);
        }

        System.out.println(methodName + "ended");
    }
}

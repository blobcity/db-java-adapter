/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */

package com.blobcity.db.search;

import com.blobcity.db.config.Credentials;
import com.blobcity.db.entity.TestTable;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sanket Sarang
 */
public class QueryTest {
    
    public QueryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Credentials.init("test", "test", "test");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of select method, of class Query.
     */
//    @Test
    public void testSelect_0args() {
        System.out.println("select");
        Query expResult = null;
        Query result = Query.select();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of select method, of class Query.
     */
//    @Test
    public void testSelect_StringArr() {
        System.out.println("select");
        String[] columnNames = null;
        Query expResult = null;
        Query result = Query.select(columnNames);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of count method, of class Query.
     */
    @Test
    public void testCount() {
        System.out.println("count");
        String expResult = "SELECT COUNT(*) FROM `test`.`TestTable`";
        String result = Query.count().from(TestTable.class).asSql();
        assertEquals(expResult, result);
    }

    /**
     * Test of table method, of class Query.
     */
//    @Test
    public void testTable() {
        System.out.println("table");
        Query expResult = null;
        Query result = Query.table(null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of from method, of class Query.
     */
//    @Test
    public void testFrom() {
//        System.out.println("from");
//        Query instance = null;
//        Query expResult = null;
//        Query result = instance.from(null);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of where method, of class Query.
     */
//    @Test
    public void testWhere() {
        System.out.println("where");
        SearchParam searchParam = null;
        Query instance = null;
        Query expResult = null;
        Query result = instance.where(searchParam);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of filter method, of class Query.
     */
//    @Test
    public void testFilter() {
        System.out.println("filter");
        String[] filterNames = null;
        Query instance = null;
        Query expResult = null;
        Query result = instance.filter(filterNames);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of orderBy method, of class Query.
     */
//    @Test
    public void testOrderBy() {
        System.out.println("orderBy");
        OrderElement[] orderElems = null;
        Query instance = null;
        Query expResult = null;
        Query result = instance.orderBy(orderElems);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of limit method, of class Query.
     */
//    @Test
    public void testLimit_int() {
        System.out.println("limit");
        int limit = 0;
        Query instance = null;
        Query expResult = null;
        Query result = instance.limit(limit);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of limit method, of class Query.
     */
//    @Test
    public void testLimit_int_int() {
        System.out.println("limit");
        int limit = 0;
        int offset = 0;
        Query instance = null;
        Query expResult = null;
        Query result = instance.limit(limit, offset);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of asJson method, of class Query.
     */
//    @Test
    public void testAsJson() {
        System.out.println("asJson");
        Query instance = null;
        JsonObject expResult = null;
        JsonObject result = instance.asJson();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of asSql method, of class Query.
     */
//    @Test
    public void testAsSql() {
        System.out.println("asSql");
        Query instance = null;
        String expResult = "";
        String result = instance.asSql();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFromTables method, of class Query.
     */
//    @Test
    public void testGetFromTables() {
        System.out.println("getFromTables");
//        Query instance = null;
//        List<Class<T>> expResult = null;
//        List<Class<T>> result = instance.getFromTables();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

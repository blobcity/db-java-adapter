/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.iconnector;

import com.blobcity.db.adapter.test.entity.User;
import com.blobcity.db.constants.Credentials;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public class CloudStorageTest {

    private final String EMAIL = "test@blobcity.com";
    private final String NAME = "Test";
    private final String NAME2 = "BlobCity";

    public CloudStorageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Credentials.getInstance().init("test", "test");
        List<Object> keys = CloudStorage.selectAll(User.class);
        System.out.println("Length: " + keys.size());
        for (Object key : keys) {
            CloudStorage.remove(User.class, key);
        }
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
     * Test of newInstance method, of class CloudStorage.
     */
    @Test
    public void testNewInstance_Class() {
        System.out.println("newInstance_Class");
        User user = CloudStorage.newInstance(User.class);
        assertNotNull(user);
        System.out.println("newInstance_Class: Successful");;
    }

    /**
     * Test of newInstance method, of class CloudStorage.
     */
    @Test
    public void testNewInstance_Class_Object() {
        System.out.println("newInstance_Class_Object");
        User user = CloudStorage.newInstance(User.class, EMAIL);
        assertNotNull(user);
        assertEquals(EMAIL, user.getEmail());
        System.out.println("newInstance_Class_Object: Successful");
    }

    /**
     * Test of insert method, of class CloudStorage.
     */
    @Test
    public void testInsert() {
        System.out.println("insert");
        User user = CloudStorage.newInstance(User.class, EMAIL);
        assertNotNull(user);
        user.setName(NAME);
        boolean result = user.insert();
        assertEquals(true, result);
        result = user.insert();
        assertEquals(false, result);
        System.out.println("insert: Sucessful");
    }

    /**
     * Test of newLoadedInstance method, of class CloudStorage.
     */
    @Test
    public void testNewLoadedInstance() {
        System.out.println("newLoadedInstance");
        Class clazz = null;
        Object pk = null;
        CloudStorage expResult = null;
        CloudStorage result = CloudStorage.newLoadedInstance(clazz, pk);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of selectAll method, of class CloudStorage.
     */
    @Test
    public void testSelectAll() {
        System.out.println("selectAll");
        Class clazz = null;
        List expResult = null;
        List result = CloudStorage.selectAll(clazz);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of contains method, of class CloudStorage.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        Class clazz = null;
        Object key = null;
        boolean expResult = false;
        boolean result = CloudStorage.contains(clazz, key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class CloudStorage.
     */
    @Test
    public void testRemove_Class_Object() {
        System.out.println("remove");
        Class clazz = null;
        Object pk = null;
        CloudStorage.remove(clazz, pk);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPk method, of class CloudStorage.
     */
    @Test
    public void testSetPk() {
        System.out.println("setPk");
        User user = CloudStorage.newInstance(User.class);
        assertNotNull(user);
        user.setPk(EMAIL);
        assertEquals(EMAIL, user.getEmail());
        System.out.println("setPk: Successful");
    }

    /**
     * Test of load method, of class CloudStorage.
     */
    @Test
    public void testLoad() {
        System.out.println("load");
        CloudStorage instance = new CloudStorageImpl();
        boolean expResult = false;
        boolean result = instance.load();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class CloudStorage.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        CloudStorage instance = new CloudStorageImpl();
        instance.save();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class CloudStorage.
     */
    @Test
    public void testRemove_0args() {
        System.out.println("remove");
        CloudStorage instance = new CloudStorageImpl();
        instance.remove();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class CloudStorageImpl extends CloudStorage {
    }
}
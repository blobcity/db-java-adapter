/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.test.entity.User;
import com.blobcity.db.constants.Credentials;
import com.blobcity.db.search.SearchParams;
import com.blobcity.db.search.SearchType;
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
    private final String EMAIL2 = "test1@blobcity.com";
    private final String NAME = "Test";
    private final String NAME2 = "BlobCity";

    public CloudStorageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Credentials.getInstance().init("test", "test");
        List<Object> keys = CloudStorage.selectAll(User.class);
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
        User user = CloudStorage.newLoadedInstance(User.class, EMAIL);
        assertEquals(NAME, user.getName());
        user = CloudStorage.newLoadedInstance(User.class, EMAIL2);
        assertNull(user);
        System.out.println("newLoadedInstance: Successful");
    }

    /**
     * Test of selectAll method, of class CloudStorage.
     */
    @Test
    public void testSelectAll() {
        System.out.println("selectAll");
        List result = CloudStorage.selectAll(User.class);
        assertEquals(1, result.size());
        assertEquals(EMAIL, result.get(0));
        System.out.println("selectAll: Successful");
    }

    /**
     * Test of contains method, of class CloudStorage.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        boolean outcome1 = CloudStorage.contains(User.class, EMAIL);//existent record
        assertEquals(true, outcome1);
        boolean outcome2 = CloudStorage.contains(User.class, EMAIL2);//in-existent record
        assertEquals(false, outcome2);
        System.out.println("contains: Successful");
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
        User user = CloudStorage.newInstance(User.class, EMAIL);
        boolean loaded = user.load();
        assertEquals(true, loaded);
        user = CloudStorage.newInstance(User.class, EMAIL2);
        loaded = user.load();
        assertEquals(false, loaded);
        System.out.println("load: Successful");
    }

    @Test
    public void testSearch() {
        System.out.println("search static");
        SearchParams searchParams = new SearchParams();
        searchParams.add("name", NAME);
        searchParams.add("name", NAME2);
        List<Object> list = CloudStorage.search(User.class, SearchType.AND, searchParams);
        assertArrayEquals(new Object[]{"me@blobcity.com"}, list.toArray());
        System.out.println("search static: Successful");
    }

    @Test
    public void testSearchAnd() {
        System.out.println("searchAnd");
        User user = CloudStorage.newInstance(User.class);
        user.setName(NAME);
        List<Object> list = user.searchAnd();
        assertArrayEquals(new Object[]{"me@blobcity.com"}, list.toArray());
        System.out.println("searchAnd: Successful");
    }

    @Test
    public void testSearchOr() {
        System.out.println("searchOr");
        User user = CloudStorage.newInstance(User.class);
        user.setName(NAME);
        List<Object> list = user.searchOr();
        assertArrayEquals(new Object[]{"me@blobcity.com"}, list.toArray());
        System.out.println("searchOr: Successful");
    }

    /**
     * Test of save method, of class CloudStorage.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        User user = CloudStorage.newLoadedInstance(User.class, EMAIL);
        assertNotNull(user);
        assertEquals(NAME, user.getName());
        user.setName(NAME2);
        user.save();
        user = CloudStorage.newLoadedInstance(User.class, EMAIL);
        assertNotNull(user);
        assertEquals(NAME2, user.getName());
        System.out.println("save: Successful");
    }

    /**
     * Test of remove method, of class CloudStorage.
     */
    @Test
    public void testRemove_Class_Object() {
        System.out.println("static remove");
        if (!CloudStorage.contains(User.class, EMAIL)) {
            fail("Cannot test remove if record to remove is not present in database. "
                    + "It is possible that some other methods responsible for data insertion on the contains"
                    + "method to check for presense of record failed.");
        }
        CloudStorage.remove(User.class, EMAIL);
        if (CloudStorage.contains(User.class, EMAIL)) {
            fail("Remove operation failed. It is possible that contains method to check presence of record "
                    + "is misbehaving.");
        }
        System.out.println("static remove: Successful");
    }

    /**
     * Test of remove method, of class CloudStorage.
     */
    @Test
    public void testRemove_0args() {
        System.out.println("instance remove");
        User user = CloudStorage.newInstance(User.class, EMAIL);
        user.insert();
        user = CloudStorage.newLoadedInstance(User.class, EMAIL);
        assertNotNull(user);
        user.remove();
        boolean contains = CloudStorage.contains(User.class, EMAIL);
        if (contains) {
            fail("Non static remove operation failed. It is possible that contains method used to check removal is misbehaving.");
        }
        System.out.println("instance remove: Successful");
    }
}
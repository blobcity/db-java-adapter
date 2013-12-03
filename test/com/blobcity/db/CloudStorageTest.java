/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.test.entity.User;
import com.blobcity.db.constants.Credentials;
import com.blobcity.db.search.SearchParams;
import com.blobcity.db.search.SearchType;
import java.util.Arrays;
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
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class CloudStorageTest {

    private final String EMAIL = "test@blobcity.com";
    private final String EMAIL2 = "test1@blobcity.com";
    private final String EMAIL3 = "temp@blobcity.com";
    private final String NAME = "Test";
    private final String NAME2 = "BlobCity";

    public CloudStorageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Credentials.getInstance().init("test", "test");
        clearTable();
    }

    private static void clearTable() {
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
        clearTable();
    }
    
    private User createSample() {
        User user = CloudStorage.newInstance(User.class, EMAIL3);
        user.setName(NAME);
        user.setCharField('c');
        user.setDoubleField(0.0011);
        user.setFloatField(3.14f);
        user.setIntField(7);
        List<Character> charList = Arrays.asList('a', 'b', 'c');
        List<Double> doubleList = Arrays.asList(1.1, 1.2, 1.3, 1.4);
        List<Float> floatList = Arrays.asList(3.1f, 3.2f, 3.3f);
        List<Integer> intList = Arrays.asList(5, 6, 7, 8);
        List<String> stringList = Arrays.asList("how", "questions", "authors", "indicator");
        user.setListCharField(charList);
        user.setListDoubleField(doubleList);
        user.setListFloatField(floatList);
        user.setListIntField(intList);
        user.setListStringField(stringList);
        return user;
    }

    /**
     * Test of newInstance method, of class CloudStorage.
     */
    @Test
    public void testNewInstance_Class() {
        System.out.println("newInstance_Class");
        User user = CloudStorage.newInstance(User.class);
        assertNotNull(user);
        System.out.println("newInstance_Class: Successful");
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
        User user = createSample();
        assertNotNull(user);
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
        User insertedUser = createSample();
        insertedUser.insert();
        User user = CloudStorage.newLoadedInstance(User.class, insertedUser.getEmail());
        assertEquals(user.getName(), insertedUser.getName());
        assertEquals(user.getCharField(), insertedUser.getCharField());
        assertEquals(user.getDoubleField(), insertedUser.getDoubleField(), 0);
        assertEquals(user.getEmail(), insertedUser.getEmail());
        assertEquals(user.getFloatField(), insertedUser.getFloatField(), 0);
        assertEquals(user.getIntField(), insertedUser.getIntField());
        assertEquals(user.getListCharField(), insertedUser.getListCharField());
        assertEquals(user.getListDoubleField(), insertedUser.getListDoubleField());
        assertEquals(user.getListFloatField(), insertedUser.getListFloatField());
        assertEquals(user.getListIntField(), insertedUser.getListIntField());
        assertEquals(user.getListStringField(), insertedUser.getListStringField());
        System.out.println("newLoadedInstance: Successful");
    }

    /**
     * Test of selectAll method, of class CloudStorage.
     */
    @Test
    public void testSelectAll() {
        System.out.println("selectAll");
        User insertedUser = createSample();
        insertedUser.insert();
        List result = CloudStorage.selectAll(User.class);
        assertEquals(1, result.size());
        assertEquals(insertedUser.getEmail(), result.get(0));
        System.out.println("selectAll: Successful");
    }

    /**
     * Test of contains method, of class CloudStorage.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        User insertedUser = createSample();
        insertedUser.insert();
        boolean outcome1 = CloudStorage.contains(User.class, insertedUser.getEmail());//existent record
        assertEquals(true, outcome1);
        boolean outcome2 = CloudStorage.contains(User.class, "foo");//in-existent record
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
        User insertedUser = createSample();
        insertedUser.insert();
        User user = CloudStorage.newInstance(User.class, insertedUser.getEmail());
        boolean loaded = user.load();
        assertEquals(true, loaded);
        user = CloudStorage.newInstance(User.class, "foo");
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
        List<Object> list = user.searchAnd();
        assertArrayEquals(new Object[]{"me@blobcity.com"}, list.toArray());
        System.out.println("searchOr: Successful");
    }

    /**
     * Test of save method, of class CloudStorage.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        User sample = createSample();
        sample.insert();
        User user = CloudStorage.newLoadedInstance(User.class, sample.getEmail());
        assertNotNull(user);
        assertEquals(sample.getName(), user.getName());
        user.setName("Name2");
        user.save();
        user = CloudStorage.newLoadedInstance(User.class, sample.getEmail());
        assertNotNull(user);
        assertEquals("Name2", user.getName());
        System.out.println("save: Successful");
    }

    /**
     * Test of remove method, of class CloudStorage.
     */
    @Test
    public void testRemove_Class_Object() {
        System.out.println("static remove");
        User sample = createSample();
        sample.insert();
        CloudStorage.remove(User.class, sample.getEmail());
        if (CloudStorage.contains(User.class, sample.getEmail())) {
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
        User user = createSample();
        user.insert();
        user = CloudStorage.newLoadedInstance(User.class, user.getEmail());
        assertNotNull(user);
        user.remove();
        boolean contains = CloudStorage.contains(User.class, user.getEmail());
        if (contains) {
            fail("Non static remove operation failed. It is possible that contains method used to check removal is misbehaving.");
        }
        System.out.println("instance remove: Successful");
    }
    /*
     * TODO
     * 1. Change of data type
     * 2. All possible PKs
     * 3. Alls PKs that are not allowed
     * 4. BigDecimal to String conversion
     * 5. Table with non-column field
     * 6. Invalid credentials. Setting credentials twice.
     * 7. Multiple PKs
     * 8. Columns with same name
     * 9. Schema altered with object not updated
     */
}
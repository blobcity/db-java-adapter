/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.adminpanel.db.bo.Column;
import com.blobcity.adminpanel.db.bo.ColumnType;
import com.blobcity.adminpanel.db.service.DbAdminService;
import com.blobcity.adminpanel.exceptions.ValidationException;
import com.blobcity.db.test.entity.User;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.exceptions.InternalAdapterException;
import com.blobcity.db.test.entity.pktests.CharTable;
import com.blobcity.db.test.entity.pktests.DoubleTable;
import com.blobcity.db.test.entity.pktests.FloatTable;
import com.blobcity.db.test.entity.TestableCloudStorage;
import com.blobcity.db.test.entity.datatype.BadTable;
import com.blobcity.db.test.entity.datatype.Table1;
import com.blobcity.db.test.entity.datatype.Table2;
import com.blobcity.db.test.entity.pktests.IntTable;
import com.blobcity.db.test.entity.pktests.LongTable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
@Ignore
public class CloudStorageTest {

    public CloudStorageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
//        Credentials.init("username", "password", "dbname");
        clearTable();
    }

    private static void clearTable() {
        List<Object> keys = CloudStorage.selectAll(User.class);
        for (Object key : keys) {
            CloudStorage.remove(User.class, key);
        }
    }

    private static void clearSecondaryTables(List<Class<? extends TestableCloudStorage>> tablesToDelete) throws InstantiationException, IllegalAccessException {
        DbAdminService service = new DbAdminService();
        for (Class<? extends TestableCloudStorage> clazz : tablesToDelete) {
            try {
                service.dropTable("test", clazz.newInstance().getTableName());
            } catch (ValidationException ex) {
            }
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
        User user = CloudStorage.newInstance(User.class, "temp@blobcity.info");
        user.setName("Test");
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
        User user = CloudStorage.newInstance(User.class, "test@blobcity.info");
        assertNotNull(user);
        assertEquals("test@blobcity.info", user.getEmail());
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
        insertedUser.setEmail("test2");
        assertTrue(insertedUser.insert());
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
        user.setPk("test@blobcity.info");
        assertEquals("test@blobcity.info", user.getEmail());
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
        System.out.println("\n\n");
        for (int i = 0; i < 100; i++) {
            System.out.println("search() HAS NOT YET BEEN TESTED");
        }
        System.out.println("\n\n");
        // TODO: Uncomment this when the database implements search
//        System.out.println("search static");
//        final Query query = Query.select("").from(User.class).where(SearchParam.create("name").eq("test@blobcity.info").or(SearchParam.create("name").eq("test2@blobcity.info")));
//        final List<User> list = CloudStorage.search(query);
//        assertArrayEquals(new Object[]{"me@blobcity.com"}, list.toArray()); // TODO: these aren't correct test responses. Someone needs to fix this based on the data that the table has
//        System.out.println("search static: Successful");
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

    @Test
    public void testPKTypes() throws ValidationException, InstantiationException, IllegalAccessException {
        DbAdminService service = new DbAdminService();
        List<Class<? extends TestableCloudStorage>> tables = new ArrayList<Class<? extends TestableCloudStorage>>();
        tables.add(IntTable.class);
        tables.add(FloatTable.class);
        tables.add(LongTable.class);
        tables.add(DoubleTable.class);
        tables.add(CharTable.class);
        clearSecondaryTables(tables);
        for (Class<? extends TestableCloudStorage> clazz : tables) {
            TestableCloudStorage table = clazz.newInstance();
            service.createTable("test", table.getStructure());
            assertTrue("Insert failed for " + table.getTableName(), table.insert());
            assertTrue("Load failed for " + table.getTableName(), table.load());
        }
        clearSecondaryTables(tables);
        //TODO list pk
    }

    @Test
    public void testChangeDataType() throws ValidationException, InstantiationException, IllegalAccessException {
        List<Class<? extends TestableCloudStorage>> tables = new ArrayList<Class<? extends TestableCloudStorage>>();
        tables.add(Table1.class);
        tables.add(Table2.class);
        clearSecondaryTables(tables);
        DbAdminService service = new DbAdminService();
        Table1 record1 = new Table1();
        service.createTable("test", record1.getStructure());

        record1.setEmail("test@blobcity.info");
        record1.setName("5.46");
        assertTrue(record1.insert());

        Table1 record2 = new Table1();
        record2.setEmail("test1@blobcity.info");
        record2.setName("abcd");
        assertTrue(record2.insert());

        com.blobcity.adminpanel.db.bo.Column col = new Column();
        col.setName("name");
        col.setType(ColumnType.FLOAT);
        assertTrue(service.alterColumn("test", Table1.TABLENAME, col));
        assertTrue(service.renameTable("test", Table1.TABLENAME, Table2.TABLENAME));

        List<String> emails = Table2.selectAll(Table2.class, String.class);
        for (String email : emails) {
            try {
                Table2 instance = Table2.newLoadedInstance(Table2.class, email);
                if (email.equals("test@blobcity.info")) {
                    assertEquals(instance.getName(), 5.46, 0.0001);
                } else {
                    fail("Unexpected record in table: " + instance.toString());
                }
            } catch (InternalAdapterException iae) {
                assertTrue("test1@blobcity.info".equals(email) && iae.getCause() != null && iae.getCause() instanceof NumberFormatException);
            }
        }
    }

    @Test
    public void testChangeCredentials() {
        try {
            Credentials.init("new_username", "new_password", "new_dbname");
        } catch (Throwable t) {
            assertTrue(t instanceof IllegalStateException);
        }
    }

    @Test
    public void testBadDataType() throws ValidationException, InstantiationException, IllegalAccessException {
        //Create table with unsuported datatype
        //insert
        //what should the outcome be?
        DbAdminService service = new DbAdminService();
        List<Class<? extends TestableCloudStorage>> deleteList = new ArrayList<Class<? extends TestableCloudStorage>>();
        deleteList.add(BadTable.class);
        clearSecondaryTables(deleteList);
        BadTable table = new BadTable();
        service.createTable("test", table.getStructure());
        table.setEmail("test@blobcity.info");
        table.setName(BigDecimal.TEN);
        table.insert(); //assertTrue?
        table.load();
        table.getName(); //?
    }
}

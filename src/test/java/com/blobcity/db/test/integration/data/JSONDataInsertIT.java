package com.blobcity.db.test.integration.data;

import com.blobcity.db.Db;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.enums.CollectionType;
import com.google.gson.JsonObject;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sanketsarang on 18/11/16.
 */
public class JSONDataInsertIT {

    @BeforeClass
    public static void setUpClass() {
        Credentials.init("localhost:10111","root","root","test");
        Assert.assertTrue("Cannot perform data insert tests as datastore creation failed", Db.createDs("test"));
        Assert.assertTrue("Cannot perform data insert tests as on-disk collection creation failed", Db.createCollection("disk", CollectionType.ON_DISK));
        Assert.assertTrue("Cannot perform data insert tests as in-memory collection creation failed", Db.createCollection("mem", CollectionType.IN_MEMORY));
        Assert.assertTrue("Cannot perform data insert tests as in-memory-non-durable collection creation failed", Db.createCollection("memnd", CollectionType.IN_MEMORY_NON_DURABLE));
    }

    @AfterClass
    public static void tearDownClass() {
        Assert.assertTrue("Failed to drop datastore after test completion. Other tests may fail.", Db.dropDs("test"));
        Credentials.unInit();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void insertOnDisk() {
        System.out.println("IT: insertOnDisk");

        /* Insert first record */
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("name","name1");
        jsonObject1.addProperty("age",45);
        Db.insertJson("disk", jsonObject1);

        /* Insert second record */
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("name","name1");
        jsonObject2.addProperty("age",45);
        jsonObject2.addProperty("address","address 1");
        Db.insertJson("disk", jsonObject2);

        /* Insert multiple records */
        Db.insertJson("disk", Arrays.asList(new JsonObject[]{jsonObject1, jsonObject2}));
    }

    @Test
    public void insertJsonInMemory() {
        System.out.println("IT: insertJsonInMemory - yet to be implemented");
    }

    @Test
    public void insertJsonInMemoryND() {
        System.out.println("IT: insertJsonInMemoryND - yet to be implemented");
    }
}

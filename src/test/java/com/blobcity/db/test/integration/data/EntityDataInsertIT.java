package com.blobcity.db.test.integration.data;

import com.blobcity.db.Db;
import com.blobcity.db.annotations.Entity;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.enums.CollectionType;
import com.blobcity.db.search.Query;
import com.blobcity.db.search.SearchParam;
import com.google.gson.JsonObject;
import org.junit.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sanketsarang on 18/11/16.
 */
public class EntityDataInsertIT {

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
    public void insertEntityOnDisk() {
        System.out.println("IT: insertEntityOnDisk");

        Assert.assertTrue("Cannot perform data insert tests as on-disk collection creation failed", Db.createCollection("User", CollectionType.ON_DISK));
        new User("name1","address1").insert();

        List<User> userList = Db.search(Query.select().from(User.class).where(SearchParam.create("name").eq("name1")));

        for(User user : userList) {
            System.out.println(user.get_id());
        }
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


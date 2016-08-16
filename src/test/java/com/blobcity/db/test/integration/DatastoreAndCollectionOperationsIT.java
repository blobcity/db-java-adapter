package com.blobcity.db.test.integration;

import com.blobcity.db.Db;
import com.blobcity.db.config.Credentials;
import org.junit.*;

/**
 * Created by sanketsarang on 01/08/16.
 */
public class DatastoreAndCollectionOperationsIT {

    @BeforeClass
    public static void setUpClass() {
        Credentials.init("localhost:10111","root","root","test");
        Assert.assertTrue("Cannot perform tests on collections as the test datastore creation failed", Db.createDs("test"));
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
    public void createAndDropCollectionWithName() {
        System.out.println("IT: createCollection(\"test\")");
        Assert.assertTrue("Failed to create collection", Db.createCollection("test"));

        System.out.println("IT: createCollection(\"test\") with duplicate name");
        Assert.assertFalse("Did not report error when creating a collection with duplicate name",
                Db.createCollection("test")); //duplicate name

        System.out.println("IT: dropCollection(\"test\")");
        Assert.assertTrue("Failed to drop collection", Db.dropCollection("test"));

        System.out.println("IT: dropCollection(\"test\") on an inexistent collection");
        Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
                Db.dropCollection("test")); //inexistent
    }

    @Test
    public void createAndDropCollectionWithSchema() {

    }

    @Test
    public void createAndDropCollectionWithStorageAndReplicationType() {

    }
}

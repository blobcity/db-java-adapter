package com.blobcity.db.test.integration;

import com.blobcity.db.CloudStorage;
import com.blobcity.db.config.Credentials;
import org.junit.*;

/**
 * Created by sanketsarang on 01/08/16.
 */
public class TableOperationsIT {

    @BeforeClass
    public static void setUpClass() {
        Credentials.init("localhost","root","root","test");
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

    @Test
    public void createCollection() {
        System.out.println("IT: createCollection(\"test\")");
        Assert.assertTrue(CloudStorage.createCollection("test"));

        /* Test for error on duplicate collection name */
        Assert.assertFalse(CloudStorage.createCollection("test"));
    }

    @Test
    public void dropCollection() {
        System.out.println("IT: dropCollection(\"test\")");
        Assert.assertTrue(CloudStorage.createCollection("test"));
        Assert.assertTrue(CloudStorage.dropCollection("test"));

        /* Test for error on delete on in-existent collection */
        Assert.assertFalse(CloudStorage.dropCollection("test"));
    }
}

package com.blobcity.db.test.integration;

import com.blobcity.db.Db;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.enums.CollectionType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by sanketsarang on 29/08/16.
 */
public class DsAndCollectionExistsIT {

    @BeforeClass
    public static void setUpClass() {
        Credentials.init("localhost:10111","root","root","ds1");
    }

    @AfterClass
    public static void tearDownClass() {
        Db.dropDs("ds1");
        Credentials.unInit();
    }

    @Test
    public void dsExistsTest() {
        System.out.println("IT: Checking ds-exists on an in-existent datastore");
        Assert.assertFalse("Reported an in-existent datastore to be existent", Db.dsExists("ds1"));

        System.out.println("IT: Checking ds-exists on an existent datastore");
        Assert.assertTrue(Db.createDs("ds1"));
        Assert.assertTrue("Reported an existent datastore to be in-existent", Db.dsExists("ds1"));

        Assert.assertTrue(Db.dropDs("ds1"));
    }

    @Test
    public void collectionExistsTest() {
        System.out.println("IT: Checking collection-exists on an in-existent datastore");
        Assert.assertFalse("Reported collection to be existent when the datastore itself was not existent", Db.collectionExists("ds1", "collection1"));

        System.out.println("IT: Checking collection-exists on an in-existent collection");
        Assert.assertTrue(Db.createDs("ds1"));
        Assert.assertFalse("Reported collection to be existent for an in-existent collection", Db.collectionExists("ds1", "collection1"));

        System.out.println("IT: Checking collection-exists on an on-disk collection");
        Assert.assertTrue(Db.createCollection("c-on-disk", CollectionType.ON_DISK));
        Assert.assertTrue("Reported an existent on-disk collection to be non-existent", Db.collectionExists("ds1", "c-on-disk"));

        System.out.println("IT: Checking collection-exists on an in-memory collection");
        Assert.assertTrue(Db.createCollection("c-in-memory", CollectionType.IN_MEMORY));
        Assert.assertTrue("Reported an existent in-memory collection to be non-existent", Db.collectionExists("ds1", "c-in-memory"));

        System.out.println("IT: Checking collection-exists on an in-memory-nd collection");
        Assert.assertTrue(Db.createCollection("c-in-memory-nd", CollectionType.IN_MEMORY_NON_DURABLE));
        Assert.assertTrue("Reported an existent in-memory-nd collection to be non-existent", Db.collectionExists("ds1", "c-in-memory-nd"));

        Assert.assertTrue(Db.dropDs("ds1"));
    }
}

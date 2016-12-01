package com.blobcity.db.test.integration.schema;

import com.blobcity.db.Db;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.enums.CollectionType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sanketsarang on 23/08/16.
 */
public class ListDatastoresAndCollectionsIT {

    @BeforeClass
    public static void setUpClass() {
        Credentials.init("localhost:10111","root","root","ds1");
    }

    @AfterClass
    public static void tearDownClass() {
        Db.dropDs("ds1");
        Db.dropDs("ds2");
        Credentials.unInit();
    }


    @Test
    public void listDatastoresTest() {

        System.out.println("IT: Testing list-ds on no datastores");
        Assert.assertArrayEquals(new Object[]{}, Db.listDs().toArray());

        System.out.println("IT: Testing list-ds on a single datastore");
        Assert.assertTrue(Db.createDs("ds1"));
        Assert.assertArrayEquals(new Object[]{"ds1"}, Db.listDs().toArray());

        System.out.println("IT: Testing list-ds on multiple datastores");
        Assert.assertTrue(Db.createDs("ds2"));
        List<String> dsList = Db.listDs();
        Collections.sort(dsList);
        Assert.assertArrayEquals(new Object[]{"ds1","ds2"}, dsList.toArray());

        /* Drop created datastores */
        Assert.assertTrue(Db.dropDs("ds1"));
        Assert.assertTrue(Db.dropDs("ds2"));
    }

    @Test
    public void listCollectionsTest() {
        System.out.println("IT: Testing list-collections on an empty ds");
        Db.createDs("ds1");
        Assert.assertArrayEquals(new Object[]{}, Db.listCollections("ds1").toArray());

        System.out.println("IT: Testing list-collections on ds with a on-disk collection");
        Db.createCollection("c-on-disk", CollectionType.ON_DISK);
        Assert.assertArrayEquals(new Object[]{"c-on-disk"}, Db.listCollections("ds1").toArray());

        System.out.println("IT: Testing list-collections on ds with a on-disk and in-memory collection");
        Db.createCollection("c-in-memory", CollectionType.IN_MEMORY);
        List<String> collectionsList = Db.listCollections("ds1");
        Collections.sort(collectionsList);
        Assert.assertArrayEquals(new Object[]{"c-in-memory","c-on-disk"}, collectionsList.toArray());

        System.out.println("IT: Testing list-collections on ds with a on-disk, in-memory and in-memory-nd collection");
        Db.createCollection("c-in-memory-nd", CollectionType.IN_MEMORY_NON_DURABLE);
        collectionsList = Db.listCollections("ds1");
        Collections.sort(collectionsList);
        Assert.assertArrayEquals(new Object[]{"c-in-memory", "c-in-memory-nd", "c-on-disk"}, collectionsList.toArray());

        Assert.assertTrue(Db.dropDs("ds1"));
    }
}

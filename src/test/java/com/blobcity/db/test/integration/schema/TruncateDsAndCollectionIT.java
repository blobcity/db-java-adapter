package com.blobcity.db.test.integration.schema;

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
public class TruncateDsAndCollectionIT {

  @BeforeClass
  public static void setUpClass() {
    Credentials.init("localhost:10111", "root", "root", "ds1");
  }

  @AfterClass
  public static void tearDownClass() {
    Db.dropDs("ds1");
    Credentials.unInit();
  }


  @Test
  public void truncateDatastoreTest() {

    System.out.println("IT: Testing truncate-ds on an empty ds");
    Assert.assertTrue(Db.createDs("ds1"));
    Assert.assertTrue("truncate-ds returned false on an already truncated ds", Db.truncateDs("ds1"));

    System.out.println("IT: Testing truncate-ds on a ds with on-disk, in-memory and in-memory-nd collections");
    Assert.assertTrue(Db.createCollection("c-on-disk", CollectionType.ON_DISK));
    Assert.assertTrue(Db.createCollection("c-in-memory", CollectionType.IN_MEMORY));
    Assert.assertTrue(Db.createCollection("c-in-memory-nd", CollectionType.IN_MEMORY_NON_DURABLE));
    Assert.assertTrue("truncate-ds failed on ds having an on-disk, in-memory and in-memory-nd collections", Db.truncateDs("ds1"));
    Assert.assertArrayEquals("list-collections returning a non-empty result after a truncate-ds operation", new Object[]{}, Db.listCollections("ds1").toArray());

    Assert.assertTrue(Db.dropDs("ds1"));
  }
}

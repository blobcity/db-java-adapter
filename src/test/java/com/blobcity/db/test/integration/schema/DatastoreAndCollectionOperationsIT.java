package com.blobcity.db.test.integration.schema;

import com.blobcity.db.Db;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.enums.CollectionType;
import com.blobcity.db.enums.ReplicationType;
import org.junit.*;

/**
 * Created by sanketsarang on 01/08/16.
 */
public class DatastoreAndCollectionOperationsIT {

  @BeforeClass
  public static void setUpClass() {
    Credentials.init("localhost:10111", "root", "root", "test");
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
  public void createAndDropDs() {
    System.out.println("IT: createDs(\"ds1\")");
    Assert.assertTrue("Failed to create datastore", Db.createDs("ds1"));

    System.out.println("IT: createDs(\"ds1\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a datastore with duplicate name", Db.createDs("ds1"));

    System.out.println("IT: dropDs(\"ds1\")");
    Assert.assertTrue("Failed to drop datastore", Db.dropDs("ds1"));

    System.out.println("IT: dropDs(\"ds1\") on an inexistent datastore");
    Assert.assertTrue("Reported an error when dropping an inexistent datastore, Should return true on no-op",
      Db.dropDs("ds1"));
  }

  @Test
  public void createAndDropCollectionWithName() {

    /* Testing for on-disk collections */
    System.out.println("IT: createCollection(\"diskCollection\", \"on-disk\")");
    Assert.assertTrue("Failed to create collection", Db.createCollection("diskCollection", CollectionType.ON_DISK));

    System.out.println("IT: createCollection(\"diskCollection\",\"on-disk\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.ON_DISK)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-non-durable\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-nd\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY_NON_DURABLE)); //duplicate name


    /* Testing for in-memory collections */
    System.out.println("IT: createCollection(\"memoryCollection\", \"in-memory\")");
    Assert.assertTrue("Failed to create collection", Db.createCollection("memoryCollection", CollectionType.IN_MEMORY));

    System.out.println("IT: createCollection(\"memoryCollection\",\"on-disk\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.ON_DISK)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-non-durable\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-nd\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY_NON_DURABLE)); //duplicate name


    /* Testing for in-memory-non-durable collections */
    System.out.println("IT: createCollection(\"memoryNDCollection\", \"in-memory-non-durable\")");
    Assert.assertTrue("Failed to create collection", Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE));

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"on-disk\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.ON_DISK)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-non-durable\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-nd\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE)); //duplicate name


    /* Dropping all created collections */
    System.out.println("IT: dropCollection(\"diskCollection\")");
    Assert.assertTrue("Failed to drop an on-disk collection", Db.dropCollection("diskCollection"));

    System.out.println("IT: dropCollection(\"memoryCollection\")");
    Assert.assertTrue("Failed to drop an in-memory collection", Db.dropCollection("memoryCollection"));

    System.out.println("IT: dropCollection(\"memoryNDCollection\")");
    Assert.assertTrue("Failed to drop an in-memory-non-durable collection", Db.dropCollection("memoryNDCollection"));


    /* Confirming redrops to be working fine */
    System.out.println("IT: dropCollection(\"diskCollection\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("diskCollection")); //inexistent

    System.out.println("IT: dropCollection(\"memoryCollection\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("memoryCollection")); //inexistent

    System.out.println("IT: dropCollection(\"memoryNDCollection\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("memoryNDollection")); //inexistent

    System.out.println("IT: dropCollection(\"neverCreated\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("neverCreated")); //inexistent
  }

  @Test
  public void createAndDropDistributedCollections() {
    /* Testing for on-disk collections */
    System.out.println("IT: createCollection(\"diskCollection\", \"on-disk\", \"DISTRIBUTED\", 0)");
    Assert.assertTrue("Failed to create collection", Db.createCollection("diskCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 0));

    System.out.println("IT: createCollection(\"diskCollection\",\"on-disk\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.ON_DISK)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-non-durable\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-nd\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY_NON_DURABLE)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"on-disk\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-non-durable\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"on-disk\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-non-durable\",  \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"on-disk\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.ON_DISK, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-non-durable\",  \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-nd\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.MIRRORED, null)); //duplicate name


    /* Testing for in-memory collections */
    System.out.println("IT: createCollection(\"memoryCollection\", \"in-memory\", \"DISTRIBUTED\", 0)");
    Assert.assertTrue("Failed to create collection", Db.createCollection("memoryCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 0));

    System.out.println("IT: createCollection(\"memoryCollection\",\"on-disk\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.ON_DISK)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-non-durable\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-nd\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY_NON_DURABLE)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"on-disk\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-non-durable\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"on-disk\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-non-durable\",  \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"on-disk\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.ON_DISK, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-non-durable\",  \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-nd\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.MIRRORED, null)); //duplicate name


    /* Testing for in-memory-non-durable collections */
    System.out.println("IT: createCollection(\"memoryNDCollection\", \"in-memory-non-durable\", \"DISTRIBUTED\", 0)");
    Assert.assertTrue("Failed to create collection", Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 0));

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"on-disk\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.ON_DISK)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-non-durable\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-nd\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"on-disk\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-non-durable\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"on-disk\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-non-durable\",  \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"on-disk\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.ON_DISK, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-non-durable\",  \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-nd\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.MIRRORED, null)); //duplicate name

    /* Dropping all created collections */
    System.out.println("IT: dropCollection(\"diskCollection\")");
    Assert.assertTrue("Failed to drop an on-disk collection", Db.dropCollection("diskCollection"));

    System.out.println("IT: dropCollection(\"memoryCollection\")");
    Assert.assertTrue("Failed to drop an in-memory collection", Db.dropCollection("memoryCollection"));

    System.out.println("IT: dropCollection(\"memoryNDCollection\")");
    Assert.assertTrue("Failed to drop an in-memory-non-durable collection", Db.dropCollection("memoryNDCollection"));


    /* Confirming redrops to be working fine */
    System.out.println("IT: dropCollection(\"diskCollection\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("diskCollection")); //inexistent

    System.out.println("IT: dropCollection(\"memoryCollection\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("memoryCollection")); //inexistent

    System.out.println("IT: dropCollection(\"memoryNDCollection\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("memoryNDollection")); //inexistent

    System.out.println("IT: dropCollection(\"neverCreated\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("neverCreated")); //inexistent
  }

  @Test
  public void createAndDropMirroredCollections() {
    /* Testing for on-disk collections */
    System.out.println("IT: createCollection(\"diskCollection\", \"on-disk\", \"MIRRORED\", null)");
    Assert.assertTrue("Failed to create collection", Db.createCollection("diskCollection", CollectionType.ON_DISK, ReplicationType.MIRRORED, null));

    System.out.println("IT: createCollection(\"diskCollection\",\"on-disk\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.ON_DISK)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-non-durable\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-nd\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY_NON_DURABLE)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"on-disk\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-non-durable\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"on-disk\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-non-durable\",  \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"on-disk\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.ON_DISK, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-non-durable\",  \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"diskCollection\",\"in-memory-nd\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("diskCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.MIRRORED, null)); //duplicate name


    /* Testing for in-memory collections */
    System.out.println("IT: createCollection(\"memoryCollection\", \"in-memory\", \"MIRRORED\", null)");
    Assert.assertTrue("Failed to create collection", Db.createCollection("memoryCollection", CollectionType.IN_MEMORY, ReplicationType.MIRRORED, null));

    System.out.println("IT: createCollection(\"memoryCollection\",\"on-disk\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.ON_DISK)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-non-durable\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-nd\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY_NON_DURABLE)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"on-disk\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-non-durable\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"on-disk\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-non-durable\",  \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"on-disk\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.ON_DISK, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-non-durable\",  \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"memoryCollection\",\"in-memory-nd\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.MIRRORED, null)); //duplicate name


    /* Testing for in-memory-non-durable collections */
    System.out.println("IT: createCollection(\"memoryNDCollection\", \"in-memory-non-durable\", \"MIRRORED\", null)");
    Assert.assertTrue("Failed to create collection", Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.MIRRORED, null));

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"on-disk\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.ON_DISK)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-non-durable\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-nd\") with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"on-disk\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-non-durable\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 0) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 0)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"on-disk\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.ON_DISK, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-non-durable\",  \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-nd\", \"DISTRIBUTED\", 1) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.DISTRIBUTED, 1)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"on-disk\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.ON_DISK, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-non-durable\",  \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY, ReplicationType.MIRRORED, null)); //duplicate name

    System.out.println("IT: createCollection(\"memoryNDCollection\",\"in-memory-nd\", \"MIRRORED\", null) with duplicate name");
    Assert.assertFalse("Did not report error when creating a collection with duplicate name",
      Db.createCollection("memoryNDCollection", CollectionType.IN_MEMORY_NON_DURABLE, ReplicationType.MIRRORED, null)); //duplicate name

    /* Dropping all created collections */
    System.out.println("IT: dropCollection(\"diskCollection\")");
    Assert.assertTrue("Failed to drop an on-disk collection", Db.dropCollection("diskCollection"));

    System.out.println("IT: dropCollection(\"memoryCollection\")");
    Assert.assertTrue("Failed to drop an in-memory collection", Db.dropCollection("memoryCollection"));

    System.out.println("IT: dropCollection(\"memoryNDCollection\")");
    Assert.assertTrue("Failed to drop an in-memory-non-durable collection", Db.dropCollection("memoryNDCollection"));


    /* Confirming redrops to be working fine */
    System.out.println("IT: dropCollection(\"diskCollection\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("diskCollection")); //inexistent

    System.out.println("IT: dropCollection(\"memoryCollection\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("memoryCollection")); //inexistent

    System.out.println("IT: dropCollection(\"memoryNDCollection\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("memoryNDollection")); //inexistent

    System.out.println("IT: dropCollection(\"neverCreated\") on an inexistent collection");
    Assert.assertTrue("Reported an error when dropping an inexistent collection, Should return true on no-op",
      Db.dropCollection("neverCreated")); //inexistent
  }
}

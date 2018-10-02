package com.blobcity.db.test.integration.data;

import com.blobcity.db.Db;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.enums.CollectionType;
import org.junit.*;

/**
 * Created by sanketsarang on 18/11/16.
 */
public class TextDataInsertIT {

  @BeforeClass
  public static void setUpClass() {
    Credentials.init("localhost:10111", "root", "root", "test");
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
  public void insertTextOnDisk() {
    System.out.println("IT: insertTextOnDisk");

    /* Insert first record */
//        String logEntry = "127.0.0.1 - - [22/Oct/2016:22:16:31 +0530] \"GET /js/controller/barController.js HTTP/1.1\" 200 1531";
//        String logEntry = "::1 - - [23/Nov/2016:21:14:10 +0530] \"GET / HTTP/1.0\" 200 11432";
//        String logEntry = "::1 - - [22/Oct/2016:20:53:05 +0530] \"OPTIONS * HTTP/1.0\" 200 -";
    String logEntry = "127.0.0.1 - - [22/Oct/2016:20:52:35 +0530] \"GET /pages/samples-geo.html HTTP/1.1\" 304 -";

    Db.insertText("disk", logEntry);
    Db.insertText("mem", logEntry);
    Db.insertText("memnd", logEntry);

  }
}

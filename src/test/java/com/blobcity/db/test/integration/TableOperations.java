package com.blobcity.db.test.integration;

import com.blobcity.db.CloudStorage;
import com.blobcity.db.config.Credentials;

/**
 * Created by sanketsarang on 01/08/16.
 */
public class TableOperations {

    public TableOperations() {
        Credentials.init("localhost","root","root","test");
    }

    public void createTable() {
        CloudStorage.createCollection("something");
    }
}

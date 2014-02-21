/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.test.entity;

import com.blobcity.adminpanel.db.bo.Table;
import com.blobcity.db.CloudStorage;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public abstract class TestableCloudStorage<T extends CloudStorage> extends CloudStorage {
    
    public abstract Table getStructure();
    public abstract String getTableName();
}

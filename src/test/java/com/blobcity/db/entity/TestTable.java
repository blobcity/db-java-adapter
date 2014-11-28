/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */

package com.blobcity.db.entity;

import com.blobcity.db.CloudStorage;
import com.blobcity.db.annotations.Entity;
import com.blobcity.db.annotations.Primary;

/**
 *
 * @author Sanket Sarang
 */

@Entity
public class TestTable extends CloudStorage {
    @Primary
    private String myPk;
    private String column1;

    public String getMyPk() {
        return myPk;
    }

    public void setMyPk(String myPk) {
        this.myPk = myPk;
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }
}

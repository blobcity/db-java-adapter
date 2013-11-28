/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.adapter.test.entity;

import com.blobcity.db.classannotations.Entity;
import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.iconnector.CloudStorage;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
@Entity(table = "User")
public class User extends CloudStorage {

    @Primary
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

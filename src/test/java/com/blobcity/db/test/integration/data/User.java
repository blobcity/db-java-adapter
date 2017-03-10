package com.blobcity.db.test.integration.data;

import com.blobcity.db.Db;
import com.blobcity.db.annotations.Entity;

/**
 * @author Sanket Sarang
 */
@Entity(ds = "test", collection = "User")
public class User extends Db {
    private String name;
    private String address;

    public User() {
        //do nothing
    }

    public User(final String name, final String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

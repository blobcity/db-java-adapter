/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.test.entity.User;
import java.util.List;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class Temp {
    
    public static void main(String[] args) {        
        List<Object> keys = CloudStorage.selectAll(User.class);
        for (Object key : keys) {
            System.out.println("Deleting key: " + key.toString());
            CloudStorage.remove(User.class, key);
        }
        User.newInstance(User.class, "temp@blobcity.info");
    }
    
}

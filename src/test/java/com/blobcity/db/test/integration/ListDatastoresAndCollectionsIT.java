package com.blobcity.db.test.integration;

import com.blobcity.db.Db;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sanketsarang on 23/08/16.
 */
public class ListDatastoresAndCollectionsIT {

    @Test
    public void listDatastoresTest() {

        System.out.println("IT: Testing list-ds on no datastores - skipping as not implemented");
        //TODO: Implement this test by testing with a non root user that does not have access to .systemdb datastore

        System.out.println("IT: Testing list-ds on a single datastore");
        Assert.assertArrayEquals(new Object[]{"test"}, Db.listDs().toArray());

        System.out.println("IT: Testing list-ds on multiple datastores");
        Assert.assertTrue(Db.createDs("ds1"));
        List<String> dsList = Db.listDs();
        Collections.sort(dsList);
        Assert.assertTrue(new ArrayList(Arrays.asList("ds", "test")).equals(dsList));
        Assert.assertTrue(Db.dropDs("ds1"));
    }
}

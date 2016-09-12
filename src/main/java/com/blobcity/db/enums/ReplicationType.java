/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.enums;

/**
 *
 * @author Prikshit Kumar
 */
public enum ReplicationType {
    /* Data distributed to match the replication factor of the cluster */
    DISTRIBUTED("distributed"),
    /* Data is fully replicated across all nodes in the cluster for the specified collection. The collection on all nodes at all
     times will have the same data */
    MIRRORED("mirrored");
    private final String type;

    ReplicationType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    
}

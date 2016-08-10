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
public enum IndexType {
    NONE("none"),
    UNIQUE("unique"),
    BTREE("btree"),
    HASHED("hashed"),
    BITMAP("bitmap");
    private String type;

    IndexType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

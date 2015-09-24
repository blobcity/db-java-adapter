/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.enums;

/**
 *
 * @author Prikshit Kumar <prikshit.kumar@blobcity.com>
 */
public enum AutoDefineType {
    NONE("none"),
    UUID("uuid"),
    TIMESTAMP("timestamp");
    private String type;

    AutoDefineType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}

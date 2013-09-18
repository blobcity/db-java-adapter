/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.classannotations;

/**
 * When associated with a BlobCityEntity, indicates whether the table schema
 * is to be auto synced with BlobCityEntity POJO structure.
 * @author sanketsarang
 */
public @interface AutoSyncTableSchema {
    public boolean allowTypeChange() default true;
    public boolean allowColumnInserts() default false;
    public boolean allowColumnDeletes() default false;
}

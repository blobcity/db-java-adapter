/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.classannotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Karishma
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BlobCityCredentials {
    public String account();
    public String token();
    public String user();
}

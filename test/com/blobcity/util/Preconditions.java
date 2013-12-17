/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.util;

/**
 * Replacement for Google Guava Preconditions, because we don't want to add that library in the project
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class Preconditions {

    public static void checkNotNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException("value must not be null");
        }
    }
    
    public static void checkNotNull(Object obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }
    
    public static void checkArgument(boolean condition) {
        if(!condition) {
            throw new IllegalArgumentException();
        }
    }
}

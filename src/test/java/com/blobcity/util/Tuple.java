/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.util;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class Tuple<I, J> {

    private I first;
    private J second;

    public Tuple(I first, J second) {
        this.first = first;
        this.second = second;
    }

    public I getFirst() {
        return first;
    }

    public void setFirst(I first) {
        this.first = first;
    }

    public J getSecond() {
        return second;
    }

    public void setSecond(J second) {
        this.second = second;
    }
}

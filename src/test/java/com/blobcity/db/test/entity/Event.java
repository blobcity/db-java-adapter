/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */

package com.blobcity.db.test.entity;

import com.blobcity.db.annotations.Entity;
import com.blobcity.db.annotations.Column;
import com.blobcity.db.annotations.Primary;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
@Entity(table = "Event")
public class Event {
    @Primary
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "day")
    private int day;
    @Column(name = "month")
    private int month;
    @Column(name = "year")
    private int year;
    
    /* Getters and setters for all fields */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

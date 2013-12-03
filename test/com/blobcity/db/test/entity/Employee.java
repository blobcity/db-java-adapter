/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.test.entity;

import com.blobcity.db.classannotations.Entity;
import com.blobcity.db.fieldannotations.Column;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
@Entity(table = "Employee")
public class Employee extends User {

    @Column(name = "empId")
    private String employeeId;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}

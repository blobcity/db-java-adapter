/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.test.entity.pktests;

import com.blobcity.adminpanel.db.bo.ColumnType;
import com.blobcity.adminpanel.db.bo.Table;
import com.blobcity.db.classannotations.Entity;
import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.test.entity.TestableCloudStorage;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
@Entity(table = DoubleTable.TABLENAME)
public class DoubleTable extends TestableCloudStorage<DoubleTable> {

    public static final String TABLENAME = "DoubleTable";
    
    @Primary
    @Column(name = "pk")
    private double pk;
    @Column(name = "name")
    private String name;

    public DoubleTable() {
        this.pk = Double.MAX_VALUE;
    }

    public double getPk() {
        return pk;
    }

    public void setPk(double pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Table getStructure() {
        Table t = new Table();
        t.setAppId("test");
        t.setName(TABLENAME);
        com.blobcity.adminpanel.db.bo.Column pkCol = new com.blobcity.adminpanel.db.bo.Column();
        pkCol.setName("pk");
        pkCol.setType(ColumnType.DOUBLE);
        pkCol.makePrimaryKey();
        com.blobcity.adminpanel.db.bo.Column nameCol = new com.blobcity.adminpanel.db.bo.Column();
        nameCol.setName("name");
        nameCol.setType(ColumnType.STRING);
        t.addColumn(pkCol);
        t.addColumn(nameCol);
        return t;
    }

    @Override
    public String getTableName() {
        return TABLENAME;
    }
}

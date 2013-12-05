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
@Entity(table = FloatTable.TABLENAME)
public class FloatTable extends TestableCloudStorage<FloatTable> {

    public static final transient String TABLENAME = "FloatTable";
    
    @Primary
    @Column(name = "pk")
    private float pk;
    @Column(name = "name")
    private String name;

    public FloatTable() {
        this.pk = Float.MAX_VALUE;
    }

    public float getPk() {
        return pk;
    }

    public void setPk(float pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Table getStructure() {
        Table t = new Table();
        t.setAppId("test");
        t.setName(TABLENAME);
        com.blobcity.adminpanel.db.bo.Column pkCol = new com.blobcity.adminpanel.db.bo.Column();
        pkCol.setName("pk");
        pkCol.setType(ColumnType.FLOAT);
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

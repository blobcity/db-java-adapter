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
@Entity(table = LongTable.TABLENAME)
public class LongTable extends TestableCloudStorage<LongTable> {

    public static final transient String TABLENAME = "LongTable";
    
    @Primary
    @Column(name = "pk")
    private long pk;
    @Column(name = "name")
    private String name;

    public LongTable() {
        pk = Long.MAX_VALUE;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
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
        pkCol.setType(ColumnType.LONG);
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

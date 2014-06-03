/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.test.entity.datatype;

import com.blobcity.adminpanel.db.bo.ColumnType;
import com.blobcity.adminpanel.db.bo.Table;
import com.blobcity.db.annotations.Entity;
import com.blobcity.db.annotations.Column;
import com.blobcity.db.annotations.Primary;
import com.blobcity.db.test.entity.TestableCloudStorage;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
@Entity(table = Table1.TABLENAME)
public class Table1 extends TestableCloudStorage<Table1> {

    public static final  transient String TABLENAME = "Table1";
    
    @Primary
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Table getStructure() {
        Table table = new Table();
        table.setName(TABLENAME);
        com.blobcity.adminpanel.db.bo.Column pkCol = new com.blobcity.adminpanel.db.bo.Column();
        pkCol.setName("email");
        pkCol.setType(ColumnType.STRING);
        pkCol.makePrimaryKey();
        com.blobcity.adminpanel.db.bo.Column nameCol = new com.blobcity.adminpanel.db.bo.Column();
        nameCol.setName("name");
        nameCol.setType(ColumnType.STRING);
        table.addColumn(pkCol);
        table.addColumn(nameCol);
        return table;
    }

    @Override
    public String getTableName() {
        return TABLENAME;
    }
}

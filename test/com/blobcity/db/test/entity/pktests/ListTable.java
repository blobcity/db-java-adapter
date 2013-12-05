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
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
@Entity(table = ListTable.TABLENAME)
public class ListTable extends TestableCloudStorage<ListTable> {

    public static final String TABLENAME = "DoubleTable";
    @Primary
    @Column(name = "pk")
    private List<Character> pk;
    @Column(name = "name")
    private String name;

    public ListTable() {
        this.pk = Arrays.asList('h', 'e', 'l', 'l', 'o');
    }

    public List<Character> getPk() {
        return pk;
    }

    public void setPk(List<Character> pk) {
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
        // We can't create a table with a List type, so setting to string
        pkCol.setType(ColumnType.STRING);
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

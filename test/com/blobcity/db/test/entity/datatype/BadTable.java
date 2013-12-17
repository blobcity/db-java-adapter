/*
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.test.entity.datatype;

import com.blobcity.adminpanel.db.bo.ColumnType;
import com.blobcity.adminpanel.db.bo.Table;
import com.blobcity.db.classannotations.Entity;
import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.test.entity.TestableCloudStorage;
import java.math.BigDecimal;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
@Entity(table = BadTable.TABLENAME)
public class BadTable extends TestableCloudStorage<BadTable> {

    public static final transient String TABLENAME = "BadTable";
    
    @Primary
    @Column(name = "email")
    private String email;
    @Column(name = "name")
    private BigDecimal name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getName() {
        return name;
    }

    public void setName(BigDecimal name) {
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
        nameCol.setType(ColumnType.FLOAT);
        table.addColumn(pkCol);
        table.addColumn(nameCol);
        return table;
    }

    @Override
    public String getTableName() {
        return TABLENAME;
    }
}

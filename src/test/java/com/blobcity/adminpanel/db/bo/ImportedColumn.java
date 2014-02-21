/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.bo;

/**
 * Represents a column to be imported (e.g. from CSV). Allows the user to change the column name etc.
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class ImportedColumn {

    private Column column;
    private String originalName;
    
    public ImportedColumn() {
        this.column = new Column();
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void makePrimaryKey() {
        column.makePrimaryKey();
    }

    public String getName() {
        return column.getName();
    }

    public void setName(String name) {
        column.setName(name);
    }

    public boolean isPrimaryKey() {
        return column.isPrimaryKey();
    }

    public void setPrimaryKey(boolean primaryKey) {
        column.setPrimaryKey(primaryKey);
    }

    public AutoDefineType getAutoDefineType() {
        return column.getAutoDefineType();
    }

    public void setAutoDefineType(AutoDefineType autoDefineType) {
        column.setAutoDefineType(autoDefineType);
    }

    public ColumnType getType() {
        return column.getType();
    }

    public void setType(ColumnType type) {
        column.setType(type);
    }

    public IndexType getIndex() {
        return column.getIndex();
    }

    public void setIndex(IndexType index) {
        column.setIndex(index);
    }    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.enums;

/**
 *
 * @author Prikshit Kumar <prikshit.kumar@blobcity.com>
 */
public enum ColumnType {
    CHAR("CHAR"),
    CHARACTER("CHARACTER"),
    CHARACTER_VARYING("CHARACTER VARYING"),
    CHAR_VARYING("CHAR VARYING"),
    VARCHAR("VARCHAR"),
    CHARACTER_LARGE_OBJECT("CHARACTER LARGE OBJECT"),
    CHAR_LARGE_OBJECT("CHAR LARGE OBJECT"),
    CLOB("CLOB"),
    NATIONAL_CHARACTER("NATIONAL CHARACTER"),
    NATIONAL_CHAR("NATIONAL CHAR"),
    NCHAR("NCHAR"),
    NATIONAL_CHARACTER_VARYING("NATIONAL CHARACTER VARYING"),
    NATIONAL_CHAR_VARYING("NATIONAL CHAR VARYING"),
    NCHAR_VARYING("NCHAR VARYING"),
    NATIONAL_CHARACTER_LARGE_OBJECT("NATIONAL CHARACTER LARGE OBJECT"),
    NCHAR_LARGE_OBJECT("NCHAR LARGE OBJECT"),
    NCLOB("NCLOB"),
    BINARY_LARGE_OBJECT("BINARY LARGE OBJECT"),
    BLOB("BLOB"),
    NUMERIC("NUMERIC"),
    DECIMAL("DECIMAL"),
    DEC("DEC"),
    SMALLINT("SMALLINT"),
    INTEGER("INTEGER"),
    INT("INT"),
    BIGINT("BIGINT"),
    FLOAT("FLOAT"),
    REAL("REAL"),
    DOUBLE_PRECISION("DOUBLE PRECISION"),
    BOOLEAN("BOOLEAN"),
    DATE("DATE"),
    TIME("TIME"),
    TIMESTAMP("TIMESTAMP"),
    INTERVAL("INTERVAL"),
    REF("REF"),
    ARRAY("ARRAY"),
    MULTISET("MULTISET"),
    ROW("ROW"),
    XML("XML"),
    LONG("LONG"),
    DOUBLE("DOUBLE"),
    STRING("STRING");
    private final String type;
    
    ColumnType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

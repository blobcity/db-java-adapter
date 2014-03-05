/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */

package com.blobcity.db.model;

import com.blobcity.db.CloudStorage;
import com.blobcity.db.classannotations.Entity;
import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.Primary;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity(table = TestModelWeirdColumns.TABLE_NAME_REFERENCE)
public class TestModelWeirdColumns extends CloudStorage {

    @Primary
    @Column
    private final transient Logger logger = Logger.getLogger(TestModelWeirdColumns.class.getName());
    @Column
    public static final String TABLE_NAME_REFERENCE = "TableModelWeird";

    @Column
    private String key;
    @Column
    private String value;
    @Column
    private String crazy;

    public TestModelWeirdColumns() {
        // do nothing
    }

    public TestModelWeirdColumns(final String parsableStr) {
        final String[] parts = parsableStr.split(",");
        key = parts[0];
        value = parts[1];
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCrazy() {
        return crazy;
    }

    public void setCrazy(String crazy) {
        this.crazy = crazy;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            logger.log(Level.FINEST, ">> obj was null");
            return false;
        }
        if (getClass() != obj.getClass()) {
            logger.log(Level.FINEST, ">> {0} != {1}", new Object[]{getClass(), obj.getClass()});
            return false;
        }
        final TestModelWeirdColumns other = (TestModelWeirdColumns) obj;
        if (!Objects.equals(this.key, other.key)) {
            logger.log(Level.FINEST, ">> keys [\"{0}\", \"{1}\"] aren''t equal [{2}]", new Object[]{this.key, other.key, key.equals(other.key)});
            return false;
        }
        return Objects.equals(this.value, other.value);
    }

}

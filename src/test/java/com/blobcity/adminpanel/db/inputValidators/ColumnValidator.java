/*
 * Copyright BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.inputValidators;

import com.blobcity.adminpanel.db.bo.Column;
import com.blobcity.adminpanel.db.bo.ColumnType;
import com.blobcity.adminpanel.db.bo.IndexType;
import com.blobcity.adminpanel.exceptions.ValidationException;
import org.apache.commons.lang3.StringUtils;

/**
 * Validation for Database columns
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class ColumnValidator {

    public static void validate(Column column) throws ValidationException {
        if (column == null) {
            throw new ValidationException("No column found");
        }
        if (StringUtils.isBlank(column.getName())) {
            throw new ValidationException("Please specify column name");
        }
        if(StringUtils.containsWhitespace(column.getName())) {
            throw new ValidationException("Column name must not contain spaces");
        }
        if (StringUtils.length(column.getName()) < 2) {
            throw new ValidationException("Column name should have a minimum of 2 characters");
        }
        if (column.getIndex() == null) {
            throw new ValidationException("Please specify column index");
        }
        if (column.getType() == null) {
            throw new ValidationException("Please specify column type");
        }
        if (column.getAutoDefineType() == null) {
            throw new ValidationException("Please specify AutoDefineType");
        }
        if (column.isPrimaryKey()) {
            if (!column.getType().isPrimaryKeyAllowed()) {
                throw new ValidationException("The type " + column.getType() + " is not valid for a primary key column");
            }
            if (!column.getIndex().equals(IndexType.UNIQUE)) {
                throw new ValidationException("Primary Key must have Index type " + IndexType.UNIQUE);
            }
        }
        switch (column.getAutoDefineType()) {
            case UUID:
                if (!column.isOfType(ColumnType.STRING)) {
                    throw new ValidationException("AutoDefineType UUID can be applied to " + ColumnType.STRING + " column types");
                }
                break;
            /*
             * case SERIAL:
             if (!column.isOfType(ColumnType.INTEGER) && !column.isOfType(ColumnType.LONG)) {
             throw new ValidationException("AutoDefineType SERIAL can only be applied to INTEGER or LONG columns");
             }
             break;
             */
            case TIMESTAMP:
                if (!column.isOfType(ColumnType.LONG)) {
                    throw new ValidationException("AutoDefineType TIMESTAMP can only be applied to LONG columns");
                }
                break;
        }
        switch (column.getIndex()) {
            case NONE:
                break;
            case BITMAP:
            case BTREE:
            case UNIQUE:
                if (!column.getType().isIndexAllowed()) {
                    throw new ValidationException("Column of type " + column.getType() + " cannot be indexed");
                }
                break;
        }
    }
}

/*
 * Copyright BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.inputValidators;

import com.blobcity.adminpanel.db.bo.Column;
import com.blobcity.adminpanel.db.bo.Table;
import com.blobcity.adminpanel.exceptions.ValidationException;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class TableValidator {

    public static void validate(Table table) throws ValidationException {
        if (table == null) {
            throw new ValidationException("No table specified");
        }
        if (StringUtils.isBlank(table.getName())) {
            throw new ValidationException("Please specify table name");
        }
        if(StringUtils.containsWhitespace(table.getName())) {
            throw new ValidationException("Table name must not contain whitespaces");
        }
        if (StringUtils.length(table.getName()) < 2) {
            throw new ValidationException("Table name must be at least 2 characters in length");
        }
        if (table.getColumns() == null || table.getColumns().isEmpty()) {
            return;
        }
        int primaryKeyCount = 0;
        for (Column col : table.getColumns()) {
            if (col.isPrimaryKey()) {
                primaryKeyCount++;
            }
            ColumnValidator.validate(col);
        }
        if (primaryKeyCount == 0) {
            throw new ValidationException("Cannot create table without a Primary Key");
        } else if (primaryKeyCount > 1) {
            throw new ValidationException("Table must contain only one Primary Key");
        }
    }
}

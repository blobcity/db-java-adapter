/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.service;

/**
 * Error codes for requests to database
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public enum ErrorCode {

    TOKEN_INVALID(ErrorType.SSO, "SSO001", "ttoken invalid"),
    PERMANENT_TOKEN_INVALID(ErrorType.SSO, "SSO002", "Permanent token invalid"),
    AUTH_SUCCESSFUL(ErrorType.SSO, "SSO101", "Authorization successful"),
    AUTH_DENIED(ErrorType.SSO, "SSO102", "Authorization denied"),
    AUTH_PENDING(ErrorType.SSO, "SSO103", "Authorization pending"),
    APP_ID_INVALID(ErrorType.APP, "APP001", "appId invalid"),
    APP_KEY_INVALID(ErrorType.APP, "APP002", "appKey invalid"),
    APP_CRED_INVALID(ErrorType.APP, "APP003", "App credentials invalid"),
    DBQEX(ErrorType.DB, "DBQEX", "Query exception"),
    //DB_NO_TABLE(ErrorType.DB, "DB001", "Table does not exist"),
    DB_NO_TABLE(ErrorType.DB, "TABLE_INVALID", "Table does not exist"),
    //DB_TABLE_NAME_CONFLICT(ErrorType.DB, "DB002", "Table with given name already exists"),
    DB_TABLE_NAME_CONFLICT(ErrorType.DB, "DUPLICATE_TABLE_NAME", "Table with given name already exists"),
    DB_INVALID_TABLE_NAME(ErrorType.DB, "DB003", "Invalid table name"),
    DB_NO_COL(ErrorType.DB, "DB101", "Column does not exist"),
    //DB_COL_NAME_CONFLICT(ErrorType.DB, "DB102", "Column with given name already exists"),
    DB_COL_NAME_CONFLICT(ErrorType.DB, "DUPLICATE_COLUMN_NAME", "Column with given name already exists"),
    DB_INVALID_COL_NAME(ErrorType.DB, "DB103", "Invalid column name"),
    DB_COL_DATA_CONVERT_FAIL(ErrorType.DB, "DB104", "Data type not convertible");

    public enum ErrorType {

        SSO,
        APP,
        DB
    }
    
    public static ErrorCode parseCode(String code) {
        for(ErrorCode err : ErrorCode.values()) {
            if(err.getErrorCode().equals(code)) {
                return err;
            }
        }
        return null;
    }

    private ErrorCode(ErrorType errorType, String errorCode, String description) {
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.description = description;
    }
    private ErrorType errorType;
    private String errorCode;
    private String description;

    public ErrorType getErrorType() {
        return errorType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }
}

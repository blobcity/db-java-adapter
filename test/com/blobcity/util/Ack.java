/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.util;

import com.blobcity.adminpanel.db.service.ErrorCode;
import org.json.JSONObject;

/**
 * Encapsulates the response for REST operations for parsing of ACK and error code
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class Ack {

    private boolean ack;
    private ErrorCode errorCode;
    private String cause;

    public Ack() {
    }

    public Ack(boolean ack, String code) {
        this.ack = ack;
        this.errorCode = ErrorCode.parseCode(code);
    }

    public Ack(JSONObject jsonObj) {
        ack = jsonObj.optInt("ack") == 1;
        String code = jsonObj.optString("code");
        this.errorCode = ErrorCode.parseCode(code);
        cause = jsonObj.optString("cause");
    }

    public static Ack unknown() {
        Ack ack = new Ack();
        ack.setAck(false);
        ack.setErrorCode(null);
        ack.setCause(null);
        return ack;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}

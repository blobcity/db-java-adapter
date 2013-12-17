/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.bo;

import java.util.Date;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a long-running background operation
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class LongOperation {

    private String opId;
    private String type;
    private long records;
    private Date timeStarted;
    private String status;
    private String logPath;
    
    public static LongOperation fromJSON(String opId, JSONObject obj) throws JSONException {
        LongOperation op = new LongOperation();
        op.setOpId(opId);
        op.setType(obj.getString("type"));
        op.setRecords(obj.getLong("records"));
        long timeStart = obj.getLong("time-started");
        op.setTimeStarted(new Date(timeStart));
        op.setStatus(obj.getString("status"));
        op.setLogPath(obj.getString("log"));
        return op;
    }

    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public Date getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(Date timeStarted) {
        this.timeStarted = timeStarted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.opId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LongOperation other = (LongOperation) obj;
        if (!Objects.equals(this.opId, other.opId)) {
            return false;
        }
        return true;
    }
}

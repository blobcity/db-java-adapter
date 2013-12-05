/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.adminpanel.db.service;

import com.blobcity.util.Ack;
import com.blobcity.adminpanel.db.bo.Column;
import com.blobcity.adminpanel.db.bo.IndexType;
import com.blobcity.adminpanel.db.bo.LongOperation;
import com.blobcity.adminpanel.db.bo.Table;
import com.blobcity.adminpanel.db.inputValidators.ColumnValidator;
import com.blobcity.adminpanel.db.inputValidators.TableValidator;
import com.blobcity.adminpanel.db.service.requests.AddColumnRequest;
import com.blobcity.adminpanel.db.service.requests.AlterColumnRequest;
import com.blobcity.adminpanel.db.service.requests.CSVImportRequest;
import com.blobcity.adminpanel.db.service.requests.CreateIndexRequest;
import com.blobcity.adminpanel.db.service.requests.CreateTableRequest;
import com.blobcity.adminpanel.db.service.requests.DropColumnRequest;
import com.blobcity.adminpanel.db.service.requests.DropIndexRequest;
import com.blobcity.adminpanel.db.service.requests.DropTableRequest;
import com.blobcity.adminpanel.db.service.requests.FetchSchemaRequest;
import com.blobcity.adminpanel.db.service.requests.ListOperationsRequest;
import com.blobcity.adminpanel.db.service.requests.ListTablesRequest;
import com.blobcity.adminpanel.db.service.requests.RenameColumnRequest;
import com.blobcity.adminpanel.db.service.requests.RenameTableRequest;
import com.blobcity.adminpanel.exceptions.ValidationException;
import com.blobcity.util.RESTUtil;
import static com.blobcity.util.Preconditions.checkArgument;
import static com.blobcity.util.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Entry point for DB schema services
 *
 * @author akshay
 */
public class DbAdminService {

    // Declarations and injections ------------------------------------------------------------------------------------
    private Logger logger = Logger.getLogger(DbAdminService.class.getName());
    private static final String DB_INTERAL_ENDPOINT = "http://dbint.blobcity.com/rest/bquery";
    private static final String DB_ENDPOINT = "http://db.blobcity.com/rest/bquery";
    private static final String appKey = "test";

    // private methods ------------------------------------------------------------------------------------------------
    private String postQuery(String json) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("q", json);
        String response = RESTUtil.doGet(DB_ENDPOINT, paramMap);
        return response;
    }

    private String postInternalQuery(String json) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("q", json);
        return RESTUtil.doGet(DB_INTERAL_ENDPOINT, map);
    }

    private Ack executeQuery(String json) {
        String response = postQuery(json);
        return RESTUtil.parseResponse(response);
    }

    /**
     * Parses the ack object and throws a validationException if a valid error code is found
     *
     * @param ack
     * @return <code>true</code> if the ack is valid (ack:1), returns <code>false</code> if the ack is invalid (ack:0)
     * and no error code is found
     * @throws ValidationException if an error code is found
     */
    private boolean parseAck(Ack ack) throws ValidationException {
        if (!ack.isAck() && ack.getErrorCode() != null) {
            throw new ValidationException(ack.getErrorCode().getDescription());
        }
        return ack.isAck();
    }

    /**
     * Parses the ack object and throws a validationException if the error code matches one of the expected error codes
     *
     * @param ack
     * @param expectedErrorCodes A list of expected error codes
     * @return <code>true</code> if the ack is valid (ack:1), returns <code>false</code> if the ack is invalid (ack:0)
     * and the error code matches none of the expected codes
     * @throws ValidationException If the error code matches one of the expected error codes
     */
    private boolean parseAck(Ack ack, ErrorCode... expectedErrorCodes) throws ValidationException {
        if (ack.isAck()) {
            return true;
        }
        if (ack.getErrorCode() == null) {
            return false;
        }
        for (ErrorCode code : expectedErrorCodes) {
            if (code.equals(ack.getErrorCode())) {
                throw new ValidationException(code.getDescription());
            }
        }
        return ack.isAck();
    }

    // public API methods ---------------------------------------------------------------------------------------------
    /**
     * Fetch all the tables for the given AppId
     *
     * @param appId The Application Id of the client app
     * @return The list of table names in the application, or an empty list if an unexpected response is received
     */
    public List<String> fetchTables(String appId) {
        checkNotNull(appId);
        JSONObject json = new ListTablesRequest(appId, appKey).createRequest();
        String response = postQuery(json.toString());
        try {
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.getInt("ack") == 1) {
                JSONArray tablesJsonArray = responseJson.getJSONArray("tables");
                List<String> tableList = new ArrayList<String>();
                if (tablesJsonArray == null) {
                    return Collections.EMPTY_LIST;
                }
                for (int i = 0; i < tablesJsonArray.length(); i++) {
                    tableList.add(tablesJsonArray.getString(i));
                }
                return tableList;
            } else {
                logger.log(Level.SEVERE, "Received ack 0 from db service. response={0}", response);
                return Collections.EMPTY_LIST;
            }
        } catch (JSONException ex) {
            logger.log(Level.SEVERE, "Could not parse response JSON. Response=" + response, ex);
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Fetches the complete table object if the table exists with a valid schema. Empty tables (without schema) will not
     * be returned
     *
     * @param appId The app Id of the client app
     * @param tableName The table name to fetch
     * @return The complete <code>Table</code> object, or <code>null</code> if the table has no columns, or if the
     * service does not return the expected response
     * @throws ValidationException if the table does not exist
     */
    public Table fetchSchema(String appId, String tableName) throws ValidationException {
        checkNotNull(appId);
        checkNotNull(tableName);
        JSONObject json = new FetchSchemaRequest(appId, appKey).createRequest(tableName);
        String response = postQuery(json.toString());
        try {
            JSONObject responseJson = new JSONObject(response);
            Ack ack = new Ack(responseJson);
            if (ack.isAck()) {
                Table table = new Table();
                table.setAppId(appId);
                table.setName(tableName);
                table.addColumnsFromPayload(responseJson.getJSONObject("p"));
                return table;
            }
            logger.log(Level.INFO, "Received ack 0 from db service. response={0}", response);
            parseAck(ack, ErrorCode.DB_NO_TABLE);
            return null;
        } catch (JSONException ex) {
            logger.log(Level.SEVERE, "Could not parse response JSON. Response=" + response, ex);
            return null;
        }
    }

    /**
     * Drops the specified table with all its data
     *
     * @param appId The app Id of the client app
     * @param tableName The name of the table to delete
     * @return <code>true</code> if the operation was successful, otherwise <code>false</code>
     * @throws ValidationException if the server returns an error code
     */
    public boolean dropTable(String appId, String tableName) throws ValidationException {
        checkNotNull(appId);
        checkNotNull(tableName);
        JSONObject json = new DropTableRequest(appId, appKey).createRequest(tableName);
        Ack ack = executeQuery(json.toString());
        return parseAck(ack);
    }

    /**
     * Restores a table recently deleted only if another table with the same name as the deleted table does not already
     * exist.
     *
     * @param appId The app Id of the client app
     * @param tableName The name of the table to be restored
     */
    public void restoreTable(String appId, String tableName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Add a table for an app.
     *
     * @param appId The app Id of the client app
     * @param table A valid table object with proper columns etc.
     * @return <code>true</code> if the operation was successful, otherwise <code>false</code>
     * @throws ValidationException if the table data is invalid or if a table with the same name exists
     */
    public boolean createTable(String appId, Table table) throws ValidationException {
        checkNotNull(appId);
        checkNotNull(table);
        TableValidator.validate(table);
        JSONObject json = new CreateTableRequest(appId, appKey).createRequest(table);
        Ack ack = executeQuery(json.toString());
        return parseAck(ack, ErrorCode.DB_TABLE_NAME_CONFLICT, ErrorCode.DB_INVALID_TABLE_NAME);

    }

    /**
     * Renames a table
     *
     * @param appId The app Id of the client app
     * @param oldTableName table name of an existent table
     * @param newTableName the name to which the table is to be renamed to
     * @return <code>true</code> if the operation is successful, otherwise <code>false</code>
     * @throws ValidationException if the server returns an error code, or a table with the same new name exists
     */
    public boolean renameTable(String appId, String oldTableName, String newTableName) throws ValidationException {
        checkNotNull(appId);
        checkArgument(StringUtils.isNotBlank(oldTableName));
        checkArgument(StringUtils.isNotBlank(newTableName));
        JSONObject json = new RenameTableRequest(appId, appKey).createRequest(oldTableName, newTableName);
        Ack ack = executeQuery(json.toString());
        return parseAck(ack, ErrorCode.DB_TABLE_NAME_CONFLICT);
    }

    /**
     * Drops the specified column of the table
     *
     * @param appId The app Id of the client app
     * @param tableName The table name of the target table
     * @param columnName The name of the column to be deleted
     * @return <code>true</code> if the operation is successful, otherwise <code>false</code>
     * @throws ValidationException if the server returns an error code
     */
    public boolean deleteColumn(String appId, String tableName, String columnName) throws ValidationException {
        checkNotNull(appId);
        checkArgument(StringUtils.isNotBlank(tableName));
        checkArgument(StringUtils.isNotBlank(columnName));
        JSONObject json = new DropColumnRequest(appId, appKey).createRequest(tableName, columnName);
        Ack ack = executeQuery(json.toString());
        return parseAck(ack);
    }

    /**
     * Adds the specified column to a table.
     *
     * @param appId The App Id of the client app
     * @param tableName The table name of the target table
     * @param column A valid column object, containing proper data
     * @return <code>true</code> if the column is added successfully, otherwise <code>false</code>
     * @throws ValidationException if the Column object is invalid
     */
    public boolean addColumn(String appId, String tableName, Column column) throws ValidationException {
        checkNotNull(appId);
        checkNotNull(tableName);
        checkNotNull(column);
        ColumnValidator.validate(column);
        JSONObject json = new AddColumnRequest(appId, appKey).createRequest(tableName, column);
        Ack ack = executeQuery(json.toString());
        return parseAck(ack, ErrorCode.DB_COL_NAME_CONFLICT);
    }

    /**
     * Alters the column as per the specified object. Altering a column may cause data loss. Does not change the column
     * index.
     *
     * @param appId The App Id of the client app
     * @param tableName The table name of the target table
     * @param column Revised column object
     * @throws ValidationException If the Column data is valid
     * @return <code>true</code> if the operation was successful, otherwise <code>false</code>
     */
    public boolean alterColumn(String appId, String tableName, Column column) throws ValidationException {
        checkNotNull(appId);
        checkNotNull(tableName);
        checkNotNull(column);
        ColumnValidator.validate(column);
        JSONObject json = new AlterColumnRequest(appId, appKey).createRequest(tableName, column);
        Ack ack = executeQuery(json.toString());
        return parseAck(ack, ErrorCode.DB_COL_NAME_CONFLICT, ErrorCode.DB_INVALID_COL_NAME);
    }

    /**
     * Alters and renames the column as per the specified parameters. This is a convenience method that calls
     * <code>renameColumn()</code> and
     * <code>alterColumn()</code>. The rename function will be called only if
     * <code>originalColumnName</code> and
     * <code>column.getName()</code> are not equal
     *
     * @param appId The Appid of the client app
     * @param tableName The name of the target table
     * @param column Revised column object
     * @param originalColumnName The original name of the column
     * @return <code>true</code> if rename and alter are successful, otherwise <code>false</code>
     * @throws ValidationException If the column object is invalid
     */
    public boolean alterColumn(String appId, String tableName, Column column, String originalColumnName) throws ValidationException {
        checkNotNull(appId);
        checkNotNull(tableName);
        checkNotNull(column);
        checkNotNull(originalColumnName);
        ColumnValidator.validate(column);
        if (!originalColumnName.equals(column.getName())) {
            boolean success = renameColumn(appId, tableName, originalColumnName, column.getName());
            if (!success) {
                return false;
            }
        }
        return alterColumn(appId, tableName, column);
    }

    /**
     * Renames the column
     *
     * @param appId The App Id of the client app
     * @param tableName The table containing the column
     * @param oldColumnName The existing name of the column
     * @param newColumnName The new name of the client
     * @return <code>true</code> true if the operation was successful, otherwise <code>false</code>
     * @throws ValidationException if the server returns an error code
     */
    public boolean renameColumn(String appId, String tableName, String oldColumnName, String newColumnName) throws ValidationException {
        checkNotNull(appId);
        checkArgument(StringUtils.isNotBlank(tableName));
        checkArgument(StringUtils.isNotBlank(oldColumnName));
        checkArgument(StringUtils.isNotBlank(newColumnName));
        JSONObject json = new RenameColumnRequest(appId, appKey).createRequest(tableName, oldColumnName, newColumnName);
        Ack ack = executeQuery(json.toString());
        return parseAck(ack);
    }

    /**
     * Creates an index on the specified column. The column must not already have an index.
     *
     * @param appId A valid appId
     * @param tableName A valid table
     * @param columnName A valid column
     * @param indexType A valid index type, compatible with the column
     * @return <code>true</code> if the Index was successfully created, otherwise <code>false</code>
     */
    public boolean createIndex(String appId, String tableName, String columnName, IndexType indexType) {
        checkArgument(StringUtils.isNotBlank(appId));
        checkArgument(StringUtils.isNotBlank(tableName));
        checkArgument(StringUtils.isNotBlank(columnName));
        checkNotNull(indexType);
        JSONObject json = new CreateIndexRequest(appId, appKey).createRequest(tableName, columnName, indexType);
        Ack ack = executeQuery(json.toString());
        return ack.isAck();
    }

    /**
     * Drops the index from the specified column.
     *
     * @param appId A valid App id
     * @param tableName A valid table
     * @param columnName A valid column
     * @return <code>true</code> if the Index was successfully dropped, otherwise <code>false</code>
     */
    public boolean dropIndex(String appId, String tableName, String columnName) {
        checkArgument(StringUtils.isNotBlank(appId));
        checkArgument(StringUtils.isNotBlank(tableName));
        checkArgument(StringUtils.isNotBlank(columnName));
        JSONObject json = new DropIndexRequest(appId, appKey).createRequest(tableName, columnName);
        Ack ack = executeQuery(json.toString());
        return ack.isAck();
    }

    /**
     * Executes a user-entered JSON query. There is no restriction on the type of statement that can be executed. As
     * such, this is not part of the "admin" service, since it only relays a JSON query to the server.
     *
     * The user-entered query need not contain appId and appKey parameters
     *
     * @param appId A valid appId
     * @param query A non-empty user-entered query
     * @return The server response in pretty JSON format
     * @throws JSONException If the user query is in an invalid format
     */
    public String executeUserQuery(final String appId, final String query) throws JSONException {
        checkArgument(StringUtils.isNotBlank(appId));
        checkArgument(StringUtils.isNotBlank(query));
        JSONObject request = new JSONObject(query);
        final String appParam = com.blobcity.adminpanel.db.service.requests.Request.APP_PARAM;
        final String keyParam = com.blobcity.adminpanel.db.service.requests.Request.KEY_PARAM;
        if (!request.has(appParam)) {
            request.put(appParam, appId);
        }
        if (!request.has(keyParam)) {
            request.put(keyParam, appKey);
        }
        String response = postQuery(request.toString());
        try {
            JSONObject responseJSON = new JSONObject(response);
            return responseJSON.toString(4);
        } catch (JSONException e) {
            return "Failed to parse server response";
        }
    }

    /**
     * Starts a CSV bulk-import on the specified file.
     *
     * @param appId The application ID
     * @param tableName The table in which to import
     * @param filePath The absolute path of the uploaded file
     * @param columnMapping (Optional), the name of the columns in the CSV mapped to user-specified column names
     * @return The operation ID of the import operation, which can be used to query its status. * *
     * Returns <code>null</code> if the operation fails on the server
     */
    public String importCSV(final String appId, final String tableName, final String filePath, Map<String, String> columnMapping) {
        checkArgument(StringUtils.isNotBlank(appId));
        checkArgument(StringUtils.isNotBlank(tableName));
        checkArgument(StringUtils.isNotBlank(filePath));
        JSONObject request = new CSVImportRequest().app(appId).t(tableName).file(filePath).columnMapping(columnMapping).createRequest();
        String response = postInternalQuery(request.toString());
        try {
            JSONObject responseJSON = new JSONObject(response);
            Ack ack = new Ack(responseJSON);
            if (ack.isAck()) {
                return responseJSON.optString("opid", null);
            } else {
                logger.log(Level.SEVERE, "Server returned ack0. request=" + request.toString() + ", response=" + response);
                return null;
            }
        } catch (JSONException ex) {
            logger.log(Level.SEVERE, "Could not parse response JSON. Response=" + response, ex);
            return null;
        }
    }

    public List<LongOperation> listOperations(final String appId, final String table) {
        checkArgument(StringUtils.isNotBlank(appId));
        JSONObject request = new ListOperationsRequest().app(appId).t(table).createRequest();
        String response = postInternalQuery(request.toString());
        try {
            JSONObject responseJSON = new JSONObject(response);
            Ack ack = new Ack(responseJSON);
            if (ack.isAck()) {
                return parseOperations(responseJSON);
            } else {
                logger.log(Level.SEVERE, "Server returned ack0. request=" + request.toString() + ", response=" + response);
                return null;
            }
        } catch (JSONException ex) {
            logger.log(Level.SEVERE, "Could not parse response JSON. Response=" + response, ex);
            return null;
        }
    }

    private List<LongOperation> parseOperations(JSONObject response) throws JSONException {
        List<LongOperation> opsList = new ArrayList<LongOperation>();
        JSONObject ops = response.getJSONObject("ops");
        Iterator keys = ops.keys();
        while(keys.hasNext()) {
            String key = (String)keys.next();
            JSONObject op = ops.getJSONObject(key);
            opsList.add(LongOperation.fromJSON(key, op));
        }
        return opsList;
    }
}

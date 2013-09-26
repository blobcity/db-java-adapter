
package com.blobcity.db.constants;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public class Database {
    
    private String url = "";
    private OperationMode mode;
    
    private Database() {
        //init operations here
        loadDatabaseUrl();
    }
    
    public static Database getInstance() {
        return DatabaseHolder.INSTANCE;
    }
    
    private static class DatabaseHolder {
        private static final Database INSTANCE = new Database();
    }
    
    public String getUrl() {
        return url;
    }
    
    public OperationMode getMode() {
        return mode;
    }
    
    private void loadDatabaseUrl() {
        //TODO: Hit server to get latest database URL
        
        url = "http://db2.blobcity.com:8080/BQueryExecuter";
    }
    
    private void identifyModeOfOperation() {
        
        /*TODO: Check if remote EJB is accessible with the application being deployed on BlobCity servers.
         * Else fall back to HTTP mode of operation.
         */
        
        mode = OperationMode.HTTP;
    }
}

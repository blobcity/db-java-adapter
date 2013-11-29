/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.bquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONObject;

/**
 * Executes a Database query.
 *
 * @author Sanket Sarang
 */
public class QueryExecuter {

    private static OperationMode operationMode = OperationMode.HTTP;
    private static final String JNDI_LOCAL_RESOURCE = "java:global/BlobCityDB/BQueryExecutorBean";
    private static final String JNDI_REMOTE_RESOURCE = "java:global/BlobCityDB/BQueryExecutorBean!com.blobcity.db.bquery.BQueryExecutorBeanRemote";
    private static InitialContext context;

    static {
        Logger.getLogger("BlobCity").log(Level.INFO, "Default operation mode is http");

        /* Check for availability of local EJB */
        try {
            context = new InitialContext();
            context.lookup(JNDI_LOCAL_RESOURCE);
            operationMode = OperationMode.LOCAL_EJB;
            Logger.getLogger("BlobCity").log(Level.INFO, "Operation mode set to no-query");
        } catch (NamingException ex) {
            //do nothing
        }

        /* Check for availability of remote EJB */
        if (operationMode == OperationMode.HTTP) {
            try {
                context = new InitialContext();
                BQueryExecutorBeanRemote bean = (BQueryExecutorBeanRemote) context.lookup(JNDI_REMOTE_RESOURCE);
                operationMode = OperationMode.REMOTE_EJB;
                Logger.getLogger("BlobCity").log(Level.INFO, "Operation mode set to remote-ejb");
            } catch (NamingException ex) {
                //do nothing
            }
        }
    }

    public String executeQuery(JSONObject queryJson) {
        switch (operationMode) {
            case LOCAL_EJB:
                return executeLocalEJB(queryJson);
            case REMOTE_EJB:
                return executeRemoteEJB(queryJson);
            case HTTP:
                return executeHTTP(queryJson);
        }
        return null;
    }

    private String executeLocalEJB(JSONObject jsonObject) {
        throw new UnsupportedOperationException("No-Query operation mode not supported in downloaded adapters. "
                + "Feature only available to applications compiled on the BlobCity Cloud. If you have arrived at this "
                + "operating mode it is very likely that you are hosting on BlobCity Cloud but did not compile your "
                + "application on the BlobCity Cloud.");
    }

    private String executeRemoteEJB(JSONObject jsonObject) {
        try {
            BQueryExecutorBeanRemote bean = (BQueryExecutorBeanRemote) context.lookup(JNDI_REMOTE_RESOURCE);
            return bean.runQuery(jsonObject.toString());
        } catch (NamingException ex) {
            throw new RuntimeException("Could not load database remote EJB for running database queries", ex);
        }
    }

    private String executeHTTP(JSONObject jsonObject) {
        BufferedReader reader = null;
        try {
            String urlString = "http://db.blobcity.com/rest/bquery?q=" + URLEncoder.encode(jsonObject.toString(), "UTF-8");
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return reader.readLine();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException("The database may be unavailable at this time for communication.", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}

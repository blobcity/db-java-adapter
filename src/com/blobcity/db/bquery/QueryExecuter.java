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
    private static final String JNDI_RESOURCE = "java:global/TestEAR/TEjb/Calculator!com.CalculatorRemote";
    private static InitialContext context;

    static {
        /* Detect mode of operation */
        try {
            context = new InitialContext();

            //TODO: Set appropriate JNDI resource URL
            BQueryExecutorBeanRemote bean = (BQueryExecutorBeanRemote) context.lookup(JNDI_RESOURCE);
        } catch (NamingException ex) {
            //do nothing
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
        throw new UnsupportedOperationException("Not yet supported.");
    }

    private String executeRemoteEJB(JSONObject jsonObject) {
        try {
            BQueryExecutorBeanRemote bean = (BQueryExecutorBeanRemote) context.lookup(JNDI_RESOURCE);
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

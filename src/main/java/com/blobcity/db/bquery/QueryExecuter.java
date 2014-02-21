/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.bquery;

import com.blobcity.db.exceptions.InternalAdapterException;
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

    private static OperationMode operationMode;
    private static final String JNDI_LOCAL_RESOURCE = "java:global/BlobCityDB/BQueryExecutorBean";
    private static final String JNDI_REMOTE_RESOURCE = "java:global/BlobCityDB/BQueryExecutorBean!com.blobcity.db.bquery.BQueryExecutorBeanRemote";
    private static BQueryExecutorBeanRemote bean;

    static {
        opmode:
        {
            /* Check for availability of local EJB */
            // TODO: If glassfish is not present, it takes upto 60 seconds per failover delaying the first query by around 2 minutes. This needs to be fixed (probably with a quicker failover)
            try {
                final InitialContext context = new InitialContext();
                context.lookup(JNDI_LOCAL_RESOURCE);
                Logger.getLogger("BlobCity").log(Level.INFO, "Operation mode of no-query available in the current context. "
                        + "Please consider compiling your application on the BlobCity Cloud to leverage no-query capabilities");
            } catch (NamingException ex) {
                //do nothing
            }

            /* Check for availability of remote EJB */
            try {
                final InitialContext context = new InitialContext();
                bean = (BQueryExecutorBeanRemote) context.lookup(JNDI_REMOTE_RESOURCE);
                operationMode = OperationMode.REMOTE_EJB;
                Logger.getLogger("BlobCity").log(Level.INFO, "Operation mode set to remote-ejb");
                break opmode;
            } catch (NamingException ex) {
                //do nothing
            }

            /* Use default operation mode of HTTP */
            operationMode = OperationMode.HTTP;
            Logger.getLogger("BlobCity").log(Level.INFO, "Using default operation mode of http");
        }
    }

    public String executeQuery(JSONObject queryJson) {
        switch (operationMode) {
            case REMOTE_EJB:
                return executeRemoteEJB(queryJson);
            default:
                return executeHTTP(queryJson);
        }
    }

    private String executeLocalEJB(JSONObject jsonObject) {
        throw new UnsupportedOperationException("No-Query operation mode not supported in downloaded adapters. "
                + "Feature only available to applications compiled on the BlobCity Cloud. If you have arrived at this "
                + "operating mode it is very likely that you are hosting on BlobCity Cloud but did not compile your "
                + "application on the BlobCity Cloud.");
    }

    private String executeRemoteEJB(JSONObject jsonObject) {
        return bean.runQuery(jsonObject.toString());
    }

    private String executeHTTP(JSONObject jsonObject) {
        BufferedReader reader = null;
        try {
            final String urlString = "http://db.blobcity.com/rest/bquery?q=" + URLEncoder.encode(jsonObject.toString(), "UTF-8");
            final URL url = new URL(urlString);
            final URLConnection connection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return reader.readLine();
        } catch (MalformedURLException ex) {
            throw new InternalAdapterException(ex);
        } catch (IOException ex) {
            throw new InternalAdapterException("The database may be unavailable at this time for communication.", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    //do nothing
                }
            }
        }
    }
}
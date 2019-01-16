/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.config.Credentials;
import com.blobcity.db.exceptions.InternalAdapterException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * Handles execution of different types of queries
 *
 * @author Karun AB
 */

class QueryExecuter {
    private QueryExecuter() {
        // do nothing
    }

    public static DbQueryResponse executeBql(final DbQueryRequest queryRequest) {
        return executeQuery(getBqlServiceUrl(queryRequest.getCredentials()), queryRequest.createPostParam());
    }

    public static DbQueryResponse executeSql(final DbQueryRequest queryRequest) {

//        try {
//
//            Class<?> cls = Class.forName( "com.blobcity.ds.bquery.SQLExecutorBean" );
//
//            final long startTime = System.currentTimeMillis();
//            Method  method;
//
//            try {
//                method = cls.getDeclaredMethod ("runQuery", String.class, String.class, String.class, String.class);
//
////                System.out.println(".....query = " + queryRequest.getQuery());
//
//                final String response;
//                try {
////                    System.out.println(".....query2 = " + queryRequest.getCredentials().getDb());
//                    response = (String)method.invoke (
//                            BeanConfigFactory.getConfigBean("com.blobcity.pom.database.engine.factory.EngineBeanConfig").getBean(SqlExecutor.class),
//                            "root", "root",
//                            ".systemdb", queryRequest.getQuery());
//
//                    final long executionTime = System.currentTimeMillis() - startTime;
//                    //logger.debug("User: \"{}\"\n"
//                    //    + "DB: \"{}\"\n"
//                    //    + "Query: \"{}\"\n\n"
//                    //    + "Response: \"{}\"\n\n"
//                    //    + "End of result.", new Object[]{username, ds, queryPayload, response});
//                    //logger.debug("Execution time (ms): " + executionTime);
//                    System.out.println("Execution time (ms) = " + executionTime);
//                    return new DbQueryResponse(response);
//                } catch (IllegalAccessException ex) {
//                    Logger.getLogger(QueryExecuter.class.getName()).log(Level.OFF, null, ex);
//                } catch (IllegalArgumentException ex) {
//                    Logger.getLogger(QueryExecuter.class.getName()).log(Level.OFF, null, ex);
//                } catch (InvocationTargetException ex) {
//                    Logger.getLogger(QueryExecuter.class.getName()).log(Level.OFF, null, ex);
//                }
//
//            } catch (NoSuchMethodException ex) {
//                Logger.getLogger(QueryExecuter.class.getName()).log(Level.OFF, null, ex);
//            } catch (SecurityException ex) {
//                Logger.getLogger(QueryExecuter.class.getName()).log(Level.OFF, null, ex);
//            }
//        }
//        catch(ClassNotFoundException ex) {
//             Logger.getLogger(QueryExecuter.class.getName()).log(Level.OFF, null, ex);
//        }
        return executeQuery(getSqlServiceUrl(queryRequest.getCredentials()), queryRequest.createPostParam());
    }

    private static DbQueryResponse executeQuery(final String serviceUrl, final String postParams) {
        BufferedReader in = null;
        DataOutputStream wr = null;
        try {
            final URL url = new URL(serviceUrl);
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en-GB;q=0.8, en;q=0.5");

            // Send post request
            con.setDoOutput(true);
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();

            con.getResponseCode();

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            final StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return new DbQueryResponse(response.toString());
        } catch (MalformedURLException ex) {
            throw new InternalAdapterException("Invalid database endpoint address format", ex);
        } catch (ProtocolException ex) {
            throw new InternalAdapterException("Invalid communication protocol with the database endpoint", ex);
        } catch (IOException ex) {
            throw new InternalAdapterException("Unable to communicate with the database at this time", ex);
        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException ex) {
                    // ignore exception
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    // ignore exception
                }
            }
        }
    }

    private static String getBqlServiceUrl(final Credentials credentials) {
        return MessageFormat.format("http://{0}/rest/bquery", credentials.getServiceAddress());
    }

    private static String getSqlServiceUrl(final Credentials credentials) {
        return MessageFormat.format("http://{0}/rest/sql", credentials.getServiceAddress());
    }
}

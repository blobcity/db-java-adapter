/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.config.Credentials;
import com.blobcity.db.exceptions.InternalAdapterException;
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
 * @author Karun AB <karun.ab@blobcity.net>
 */
class QueryExecuter {

    private QueryExecuter() {
        // do nothing
    }

    public static String executeBql(final DbQueryRequest queryRequest) {
        return executeQuery(getBqlServiceUrl(queryRequest.getCredentials()), queryRequest.createPostParam());
    }

    public static String executeSql(final DbQueryRequest queryRequest) {
        return executeQuery(getSqlServiceUrl(queryRequest.getCredentials()), queryRequest.createPostParam());
    }

    private static String executeQuery(final String serviceUrl, final String postParams) {
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

            return response.toString();
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

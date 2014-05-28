/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db;

import com.blobcity.db.exceptions.InternalAdapterException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import org.json.JSONObject;

/**
 * Executes a Database query.
 *
 * @author Sanket Sarang
 */
class QueryExecuter {

    public String executeQuery(JSONObject queryJson) {
        return executeHTTP(queryJson);
    }

    public String executeSql(final JSONObject queryJson) {
        return executeSqlHttp(queryJson);
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

    private String executeSqlHttp(final JSONObject jsonObject) {
        BufferedReader in = null;
        DataOutputStream wr = null;

        try {
            final URL url = new URL("http://db.blobcity.com/rest/sql");
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            final String username = jsonObject.getString(QueryConstants.USERNAME);
            final String password = jsonObject.getString(QueryConstants.PASSWORD);
            final String db = jsonObject.getString(QueryConstants.DB);
            final String query = jsonObject.getString(QueryConstants.PAYLOAD);

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en-GB;q=0.8, en;q=0.5");

            final String urlParameters = MessageFormat.format("username={0}&password={1}&db={2}&q={3}", username, password, db, query);

            // Send post request
            con.setDoOutput(true);
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
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
            throw new InternalAdapterException("Unable to communicate with the database at this time", ex.getCause());
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
}

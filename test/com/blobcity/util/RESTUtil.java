/*
 * Copyright 2013 BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Akshay Dewan <akshay.dewan@blobcity.net>
 */
public class RESTUtil {

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doGet(String url, Map<String, String> params) {
        Logger logger = Logger.getLogger(RESTUtil.class.getName());
        String response = "";
        try {
            URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                addParams(builder, params);
            }
            URI uri = builder.build();
            logger.log(Level.FINE, "GET query: {0}", uri.toString());
            response = Request.Get(uri).execute().returnContent().asString();
        } catch (IOException ex) {
            logger.log(Level.INFO, "Could not get response from server", ex);
        } catch (URISyntaxException ex) {
            logger.log(Level.INFO, "URI Syntax incorrect: " + url, ex);
        }
        logger.log(Level.FINE, "got response: {0}", response);
        return response;
    }

    public static Ack parseResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return new Ack(jsonObject);
        } catch (JSONException ex) {
            Logger.getLogger(RESTUtil.class.getName()).log(Level.SEVERE, "Could not parse response JSON. Response=" + response, ex);
            return Ack.unknown();
        }
    }

    public static String doPost(String url, String json, Map<String, String> urlParams) {
        Logger logger = Logger.getLogger(RESTUtil.class.getName());
        String response = "";
        try {
            URIBuilder builder = new URIBuilder(url);
            if (urlParams != null) {
                addParams(builder, urlParams);
            }
            URI uri = builder.build();
            logger.log(Level.FINE, "POST query: {0}", uri.toString());
            response = Request.Post(uri)
                    .body(new StringEntity(json, ContentType.APPLICATION_JSON))
                    .execute().returnContent().asString();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not get response from server", ex);
        } catch (URISyntaxException ex) {
            logger.log(Level.INFO, "URI Syntax incorrect: " + url, ex);
        }
        logger.log(Level.FINE, "got response: {0}", response);
        return response;
    }

    public static String doPost(String url, String json) {
        return doPost(url, json, null);
    }

    private static void addParams(URIBuilder builder, Map<String, String> params) {
        if (params == null) {
            return;
        }
        for (Entry<String, String> entry : params.entrySet()) {
            builder.addParameter(entry.getKey(), entry.getValue());
        }
    }

    private static List<NameValuePair> createParams(Map<String, String> map) {
        if (map == null) {
            return Collections.EMPTY_LIST;
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Entry<String, String> entry : map.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return params;
    }
}

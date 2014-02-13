/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.util;

import java.util.List;

/**
 * Utility class to perform operations on {@link String}
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
public class StringUtil {

    /**
     * Joins a {@link List} of {@link String}s into a single {@link String} separated by a separator. Internally consumes
     * {@link #join(java.util.List, java.lang.String)} with a default separator of {@code ","}
     *
     * @see #join(java.util.List, java.lang.String)
     * @param list values to be joined
     * @return joined {@link String} for of {@code list} if it is non {@code null}, else {@code null}
     */
    public static String join(final List<String> list) {
        return join(list, ",");
    }

    /**
     * Joins a {@link List} of {@link String}s into a single {@link String} separated by a separator. Internally consumes
     * {@link #join(java.util.List, java.lang.String, java.lang.String)} with a default {@code defaultVal} of {@code null}
     *
     * @param list values to be joined
     * @param separator for values of the list
     * @return joined {@link String} for of {@code list} if it is non {@code null}, else {@code null}
     */
    public static String join(final List<String> list, final String separator) {
        return join(list, separator, null);
    }

    /**
     * Joins a {@link List} of {@link String}s into a single {@link String} separated by a {@code seperator}.
     *
     * @param list values to be joined
     * @param separator separator for values of the list
     * @param defaultVal value to be returned if the {@code list} is {@code null} or empty
     * @return joined {@link String} for of {@code list} if it is non {@code null}, else {@code defaultValue}
     */
    public static String join(final List<String> list, final String separator, final String defaultVal) {
        if (list == null || list.isEmpty()) {
            return defaultVal;
        }

        final StringBuffer sb = new StringBuffer();
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            sb.append(list.get(i).toString());
            if (i < size - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
}

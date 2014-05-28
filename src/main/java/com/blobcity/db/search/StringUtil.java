/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import java.util.List;

/**
 * Utility class to perform operations on {@link String}.
 *
 * This class is default to ensure projects that include this class do not see this class.
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
     * @return joined {@link String} of {@code list} if it is non {@code null}, else {@code null}
     */
    public static String join(final List<String> list) {
        return join(list, ",");
    }

    /**
     * Joins a {@link List} of {@link String}s into a single {@link String} separated by a separator. Internally consumes
     * {@link #join(java.util.List, java.lang.String, java.lang.String)} with a default {@code defaultVal} of {@code null}
     *
     * @see #join(java.util.List, java.lang.String, java.lang.String)
     * @param list values to be joined
     * @param separator for values of the list
     * @return joined {@link String} of {@code list} if it is non {@code null}, else {@code null}
     */
    public static String join(final List<String> list, final String separator) {
        return join(list, separator, null);
    }

    /**
     * Joins a {@link List} of {@link String}s into a single {@link String} separated by a {@code seperator}. Internally consumes
     * {@link #join(java.util.List, java.lang.String, java.lang.String, java.lang.String)} with a default {@code escapeChar} as an empty {@link String} i.e.
     * {@code ""}
     *
     * @see #join(java.util.List, java.lang.String, java.lang.String, java.lang.String)
     * @param list values to be joined
     * @param separator separator for values of the list
     * @param defaultVal value to be returned if the {@code list} is {@code null} or empty
     * @return joined {@link String} of {@code list} if it is non {@code null}, else {@code defaultValue}
     */
    public static String join(final List<String> list, final String separator, final String defaultVal) {
        return join(list, separator, defaultVal, "");
    }

    /**
     * Joins a {@link List} of {@link String}s into a single {@link String} separated by a {@code seperator} and escaped by {@code escapeChar}.
     *
     * @param list values to be joined
     * @param separator separator for values of the list
     * @param defaultVal value to be returned if the {@code list} is {@code null} or empty
     * @param escapeChar character (as {@link String}) to be used to escape the values in the {@code list}
     * @return joined {@link String} of {@code list} by escaping each {@link String} using {@code escapeChar} if it is non {@code null}, else
     * {@code defaultValue}
     */
    public static String join(final List<String> list, final String separator, final String defaultVal, String escapeChar) {
        if (list == null || list.isEmpty()) {
            return defaultVal;
        }

        if (escapeChar == null) {
            escapeChar = "";
        } else if (escapeChar.length() > 1) {
            escapeChar = String.valueOf(escapeChar.charAt(0));
        }

        final StringBuffer sb = new StringBuffer();
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            sb.append(escapeChar).append(list.get(i));
            if (i < size - 1) {
                sb.append(escapeChar).append(separator);
            }
        }
        return sb.append(escapeChar).toString();
    }
}
